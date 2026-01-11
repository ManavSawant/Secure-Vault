package com.vault.secure_vault.dto.Auth;

import lombok.Builder;

/**
 * Response returned after password reset operation.
 */
@Builder
public record PasswordResetResponseDTO(
        boolean success,
        String message
) {}
