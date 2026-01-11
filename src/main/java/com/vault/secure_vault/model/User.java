package com.vault.secure_vault.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * Represents a user account in the Secure Vault system.
 *
 * <p>This entity stores authentication data, profile information,
 * storage usage, and credit balance for a user.</p>
 *
 * <p>This is a core domain model. Business rules related to users
 * (credits, storage, deletion) should be enforced here or in the service layer,
 * never in controllers.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user_profiles")
public class User {
    /**
     * Unique identifier of the user (MongoDB ObjectId).
     */
    @Id
    private String id;
    /**
     * Unique email address used as username for authentication.
     */
    @Indexed(unique = true)
    private String email;
    /**
     * Encrypted (hashed) password of the user.
     */
    private String password;
    /**
     * User's first name.
     */
    private String firstName;
    /**
     * User's last name.
     */
    private String lastName;
    /**
     * Optional URL to user's profile photo.
     */
    private String photoUrl;
    /**
     * Number of credits available to the user.
     * Credits are used to purchase additional storage.
     */
    private int credits;
    /**
     * Total storage currently used by the user in bytes.
     */
    private long storageUsed;
    /**
     * Maximum storage limit allocated to the user in bytes.
     */
    private long storageLimit;

    /**
     * Timestamp when the user account was created.
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Soft delete flag. If true, the user is considered deleted but data is retained.
     */
    private boolean isDeleted;

    /**
     * Increases the user's used storage by the given number of bytes.
     *
     * @param bytes number of bytes to add
     */
    public void addUsedStorage(long bytes) {
        this.storageUsed += bytes;
    }

    /**
     * Decreases the user's used storage by the given number of bytes.
     * Storage will never go below zero.
     *
     * @param bytes number of bytes to remove
     */
    public void removeUsedStorage(long bytes) {
        this.storageUsed -= bytes;
        if (this.storageUsed < 0) {
            this.storageUsed = 0;
        }
    }

    /**
     * Marks the user account as deleted (soft delete).
     */
    public void markAsDeleted() {
        this.isDeleted = true;
    }

    /**
     * Deducts credits from the user.
     *
     * @param amount number of credits to spend
     * @throws IllegalStateException if user does not have enough credits
     */
    public void spendCredits(int amount) {
        if (this.credits < amount) {
            throw new IllegalStateException("Not enough credits");
        }
        this.credits -= amount;
    }

    /**
     * Increases the user's storage limit.
     *
     * @param bytes number of bytes to add to storage limit
     */
    public void increaseStorageLimit(long bytes) {
        this.storageLimit += bytes;
    }
}
