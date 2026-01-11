package com.vault.secure_vault.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for registering a new user account.
 *
 * <p>Contains only fields required during signup.</p>
 */
public record UserRegistrationRequestDTO(

        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "last name is required")
        String lastName,


        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {}