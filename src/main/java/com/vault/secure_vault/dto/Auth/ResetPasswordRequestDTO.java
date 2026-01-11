package com.vault.secure_vault.dto.Auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Response returned after password reset operation.
 */
public record ResetPasswordRequestDTO(
        @NotBlank String token,
        @NotBlank String newPassword
) {}
