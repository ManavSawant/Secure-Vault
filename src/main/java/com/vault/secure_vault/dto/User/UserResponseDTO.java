package com.vault.secure_vault.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private int credits;
    private Instant createdAt;
}
