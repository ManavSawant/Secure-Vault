package com.vault.secure_vault.controller;

import com.vault.secure_vault.Auth.AuthResult;
import com.vault.secure_vault.dto.Auth.AuthResponseDTO;
import com.vault.secure_vault.Auth.AuthService;
import com.vault.secure_vault.dto.Auth.RefreshTokenRequestDTO;
import com.vault.secure_vault.dto.Auth.UserLoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Authentication APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Login",
            description = "Authenticate user and return access + refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<@NotNull AuthResponseDTO> login(
            @Valid @RequestBody UserLoginRequestDTO LoginRequestDTO
    ){
        AuthResult result = authService.login(LoginRequestDTO);
        return ResponseEntity.ok(mapToAuthResponse(result));
    }


    @Operation(
            summary = "Logout",
            description = "Invalidate refresh token"
    )
    @PostMapping("/logout")
    public ResponseEntity<@NotNull Void> logout(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ){
        authService.logout(requestDTO.refreshToken());
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Refresh access token",
            description = "Generate new access token using refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<@NotNull AuthResponseDTO> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ){
        AuthResult result =
                authService.refreshToken(requestDTO.refreshToken());
        return ResponseEntity.ok(mapToAuthResponse(result));
    }


    private AuthResponseDTO mapToAuthResponse(AuthResult result) {
        return AuthResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken().getToken())
                .tokenType("Bearer")
                .expiresIn(result.expiration())
                .build();
    }
}

