package com.vault.secure_vault.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
       @NotBlank String refreshToken
) {
}
