package com.vault.secure_vault.dto.File;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;

@Data
@Builder
public class FileUploadResponseDTO {
    private String fileId;
    private String fileName;
    private String contentType;
    private long size;
    private Instant updatedAt;
}
