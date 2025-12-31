package com.vault.secure_vault.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "files")
public class FileMetadata {
    @Id
    private String id;

    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private long size;
    private int version;

    private String ownerEmail;

    @CreatedDate
    private Instant createdAt;

    private boolean deleted;
    private Instant deletedAt;
}
