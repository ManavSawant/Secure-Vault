package com.vault.secure_vault.Auth;

import com.vault.secure_vault.security.JwtService;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserLoginResponseDTO login(UserLoginRequestDTO loginDTO){

        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("Invalid credentials"));

        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(loginDTO.getEmail());
        String refreshToken = jwtService.generateToken(loginDTO.getEmail());

        return UserLoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
