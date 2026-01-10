package com.vault.secure_vault.dto.Auth;

public record PasswordResetResponseDTO(
        boolean success,
        String message
) {}
