package com.vault.secure_vault.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserRegistrationRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;
    private String lastName;
    private String photoUrl;

    @Email(message = "Invalid email formate")
    @NotBlank(message = "Email is required")
    private String email;


    @NotBlank(message = "password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

}
