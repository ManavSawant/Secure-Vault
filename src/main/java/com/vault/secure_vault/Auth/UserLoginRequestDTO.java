package com.vault.secure_vault.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDTO {

    @Email(message = "Invalid email")
    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}
