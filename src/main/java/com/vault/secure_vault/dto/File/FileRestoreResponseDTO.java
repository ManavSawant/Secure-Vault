package com.vault.secure_vault.dto.File;

import lombok.Builder;

import java.time.Instant;
@Builder
public record FileRestoreResponseDTO(
        String fileId,
        String fileName,
        boolean restored,
        Instant restoredAt
) {}
