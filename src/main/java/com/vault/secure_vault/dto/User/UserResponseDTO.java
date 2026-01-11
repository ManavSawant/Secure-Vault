package com.vault.secure_vault.dto.User;


import lombok.Builder;

import java.time.Instant;

/**
 * Response DTO representing a user's public profile information.
 *
 * <p>Used in API responses. Never expose sensitive fields like password.</p>
 */
@Builder
public record UserResponseDTO (
        String id,
        String email,
        String firstName,
        String lastName,
        String photoUrl,
        int credits,
        long storageUsed,
        long storageLimit,
        Instant createdAt
) {}
