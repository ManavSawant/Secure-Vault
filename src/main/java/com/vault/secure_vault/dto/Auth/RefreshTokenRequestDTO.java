package com.vault.secure_vault.dto.Auth;

import jakarta.validation.constraints.NotBlank;
/**
 * Request DTO for refreshing JWT access token.
 * Contains the refresh token issued during login.
 */
public record RefreshTokenRequestDTO(
       @NotBlank String refreshToken
) {}
