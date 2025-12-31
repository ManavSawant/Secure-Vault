package com.vault.secure_vault.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String photoUrl;
}
