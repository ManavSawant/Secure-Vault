package com.vault.secure_vault.dto.File;

import lombok.Builder;
import java.time.Instant;

/**
 * Response returned after a file is successfully uploaded.
 */
@Builder
public record FileUploadResponseDTO(
        String fileId,
        String fileName,
        String contentType,
        long size,
        Instant updatedAt
) {}

