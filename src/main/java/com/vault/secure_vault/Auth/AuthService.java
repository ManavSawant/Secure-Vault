package com.vault.secure_vault.Auth;


import com.vault.secure_vault.dto.Auth.UserLoginRequestDTO;
import com.vault.secure_vault.model.PasswordResetToken;
import com.vault.secure_vault.model.RefreshToken;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.PasswordResetTokenRepository;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.security.CustomUserDetailsService;
import com.vault.secure_vault.security.JwtService;
import com.vault.secure_vault.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Transient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthResult login(UserLoginRequestDTO request) {

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.email());

        String accessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken =
                refreshTokenService.create(userDetails.getUsername(), 7);


        return new AuthResult(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenTtlSeconds()
        );
    }

    public AuthResult refreshToken(String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.validateToken(refreshTokenValue);

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        refreshToken.getUserEmail()
                );

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return new AuthResult(
                newAccessToken,
                refreshToken,
                jwtService.getAccessTokenTtlSeconds()
        );
    }

    public void logout(String refreshTokenValue) {
        RefreshToken token =
                refreshTokenService.validateToken(refreshTokenValue);
        refreshTokenService.revokeToken(token);
    }

    @Transactional
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        passwordResetTokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiryData(Instant.now().plusSeconds(15*60))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

    }

    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or used token"));

        if (resetToken.getExpiryData().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
