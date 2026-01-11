package com.vault.secure_vault.dto.User;
/**
 * Request DTO for updating user profile information.
 *
 * <p>All fields are optional. Only non-null fields should be updated.</p>
 */
public record UserProfileUpdateDTO(
         String firstName,
         String lastName,
         String photoUrl
) {}
