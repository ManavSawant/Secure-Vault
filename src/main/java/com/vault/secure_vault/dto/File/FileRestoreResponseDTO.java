package com.vault.secure_vault.dto.File;

import lombok.Builder;
import java.time.Instant;

/**
 * Response returned after a file is successfully restored.
 */
@Builder
public record FileRestoreResponseDTO(
        String fileId,
        String fileName,
        int version,
        boolean restored,
        Instant restoredAt
) {}
