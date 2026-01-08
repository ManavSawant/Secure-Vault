package com.vault.secure_vault.dto.Auth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserLoginRequestDTO(
        @Email(message = "Invalid email")
        @NotBlank(message = "email required")
        String email,

        @NotBlank(message = "password is required")
        String password
) {}
