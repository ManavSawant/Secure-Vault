package com.vault.secure_vault.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for initiating forgot password flow.
 * Contains the user's registered email.
 */
public record ForgotPasswordRequestDTO(

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email
) {}
