package com.vault.secure_vault.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents a refresh token used to obtain new JWT access tokens.
 * <p>
 * Each user can have only one active refresh token at a time.
 * Refresh tokens can be revoked and have an expiration time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {
    /**
     * Unique identifier of the refresh token document.
     */
    @Id
    private String id;
    /**
     * Secure random token value.
     */
    private String token;

    /**
     * Email of the user to whom this refresh token belongs.
     */
    private String userEmail;

    /**
     * Expiration timestamp of the refresh token.
     * After this time, the token is considered invalid.
     */
    private Instant expiryDate;

    /**
     * Indicates whether the token has been revoked.
     * Revoked tokens must not be accepted.
     */
    private boolean revoked;
}
