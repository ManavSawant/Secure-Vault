package com.vault.secure_vault.controller;

import com.vault.secure_vault.Auth.AuthResult;
import com.vault.secure_vault.dto.Auth.*;
import com.vault.secure_vault.Auth.AuthService;
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

/**
 * Handles all authentication related operations such as:
 * - Login
 * - JWT token refresh
 * - Logout
 * - Forgot & Reset password flows
 */
@Tag(name = "Auth", description = "Authentication APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Authenticates a user and issues JWT tokens.
     *
     * @param LoginRequestDTO Login credentials (email & password)
     * @return AuthResponseDTO containing access and refresh tokens
     */
    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns access & refresh tokens"
    )
    @PostMapping("/login")
    public ResponseEntity<@NotNull AuthResponseDTO> login(
            @Valid @RequestBody UserLoginRequestDTO LoginRequestDTO
    ){
        AuthResult result = authService.login(LoginRequestDTO);
        return ResponseEntity.ok(mapToAuthResponse(result));
    }

    /**
     * Logs out the user by invalidating refresh token.
     *
     * @param requestDTO Refresh token request
     * @return Success message
     */
    @Operation(
            summary = "Logout user",
            description = "Invalidates refresh token and logs out the user"
    )
    @PostMapping("/logout")
    public ResponseEntity<@NotNull Void> logout(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ){
        authService.logout(requestDTO.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Refreshes JWT access token using a valid refresh token.
     *
     * @param requestDTO Refresh token request
     * @return New access token response
     */
    @Operation(
            summary = "Refresh token",
            description = "Generates a new access token using refresh token"
    )
    public ResponseEntity<@NotNull AuthResponseDTO> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ){
        AuthResult result =
                authService.refreshToken(requestDTO.refreshToken());
        return ResponseEntity.ok(mapToAuthResponse(result));
    }

    /**
     * Initiates forgot password flow by generating reset token.
     *
     * @param request Forgot password request (email)
     * @return Success message
     */
    @Operation(
            summary = "Forgot password",
            description = "Generates password reset token and sends email"
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponseDTO> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestDTO request
    ) {
        authService.forgotPassword(request.email());

        return ResponseEntity.ok(
                new PasswordResetResponseDTO(true, "Password reset link sent")
        );
    }


    /**
     * Resets user password using valid reset token.
     *
     * @param request Reset password request (token + new password)
     * @return Success message
     */
    @Operation(
            summary = "Reset password",
            description = "Resets user password using reset token"
    )
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponseDTO> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDTO request
    ) {
        authService.resetPassword(request.token(), request.newPassword());

        return ResponseEntity.ok(
                new PasswordResetResponseDTO(true, "Password updated successfully")
        );
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

