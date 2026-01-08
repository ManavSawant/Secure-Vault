package com.vault.secure_vault.dto.File;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FileVersionResponseDTO(
        String fileId,
        int version,
        long size,
        boolean isLatest,
        boolean deleted,
        Instant createdAt
) {
}
