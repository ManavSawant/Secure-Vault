package com.vault.secure_vault.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDTO(

        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        String photoUrl,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {}