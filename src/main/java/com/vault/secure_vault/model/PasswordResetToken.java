package com.vault.secure_vault.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents a one-time password reset token used for resetting user passwords.
 * <p>
 * Each token is associated with a user's email and has an expiry time.
 * Once used, the token is marked as used and cannot be reused.
 */
@Document(collection = "password_reset_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    /**
     * Unique identifier for the reset token document.
     */
    @Id
    private String id;

    /**
     * Email of the user who requested password reset.
     */
    private String email;

    /**
     * Secure random token used for password reset verification.
     */
    private String token;

    /**
     * Expiry timestamp for the reset token.
     * After this time, the token is considered invalid.
     */
    private Instant expiryDate;

    /**
     * Indicates whether this token has already been used.
     * Prevents reuse of the same token.
     */
    private boolean used;
}
