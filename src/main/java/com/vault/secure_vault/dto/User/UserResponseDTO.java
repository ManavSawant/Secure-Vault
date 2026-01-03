package com.vault.secure_vault.dto.User;


import lombok.Builder;

import java.time.Instant;

@Builder
public record UserResponseDTO (
        String id,
        String email,
        String firstName,
        String lastName,
        String photoUrl,
        int credits,
        long storageLimit,
        Instant createdAt
) {}
