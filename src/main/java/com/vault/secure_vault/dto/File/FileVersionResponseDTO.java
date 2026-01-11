package com.vault.secure_vault.dto.File;

import lombok.Builder;
import java.time.Instant;

/**
 * Represents a single version of a file in version history.
 */
@Builder
public record FileVersionResponseDTO(
        String fileId,
        int version,
        long size,
        boolean Latest,
        boolean deleted,
        Instant createdAt
) {}
