package com.vault.secure_vault.Auth;

import com.vault.secure_vault.dto.Auth.AuthResponseDTO;
import com.vault.secure_vault.dto.Auth.RefreshTokenRequestDTO;
import com.vault.secure_vault.dto.Auth.UserLoginRequestDTO;
import com.vault.secure_vault.model.RefreshToken;
import com.vault.secure_vault.security.CustomUserDetailsService;
import com.vault.secure_vault.security.JwtService;
import com.vault.secure_vault.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponseDTO login(UserLoginRequestDTO request) {

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


        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(15 * 60)
                .build();

    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {

        RefreshToken token = refreshTokenService.validateToken(request.refreshToken());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(token.getUserEmail());

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(token.getToken())
                .tokenType("Bearer")
                .expiresIn(900)
                .build();
    }

    public void logout(RefreshTokenRequestDTO request) {
        RefreshToken token = refreshTokenService.validateToken(request.refreshToken());
        refreshTokenService.revokeToken(token);
    }
}
