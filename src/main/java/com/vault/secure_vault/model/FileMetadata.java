package com.vault.secure_vault.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents metadata for a file stored in the system.
 * <p>
 * This entity does NOT store the actual file bytes.
 * It only tracks:
 * - ownership
 * - versioning
 * - soft delete state
 * - storage references
 *
 * Actual file content is stored in physical storage (local/S3/etc).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "files")
public class FileMetadata {

    /**
     * Unique identifier of the file metadata record.
     */
    @Id
    private String id;

    /**
     * Original filename as uploaded by the user.
     * Example: resume.pdf
     */
    private String originalFilename;

    /**
     * Internal stored filename/path used in physical storage.
     * Example: user@email.com/uuid_resume.pdf
     * <p>
     * This must NEVER be exposed to the client.
     */
    private String storedFilename;

    /**
     * MIME type of the file.
     * Example: application/pdf, image/png
     */
    private String contentType;

    /**
     * File size in bytes.
     */
    private long size;

    /**
     * Version number of this file.
     * Starts from 1 and increments for each new upload of the same filename.
     */
    private int version;

    /**
     * Email of the user who owns this file.
     * Used for access control.
     */
    private String ownerEmail;

    /**
     * Timestamp when this file version was created.
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Soft delete flag.
     * true = file is logically deleted but still exists in storage.
     */
    private boolean deleted;

    /**
     * Timestamp when the file was soft deleted.
     * Null if not deleted.
     */
    private Instant deletedAt;

    /**
     * Indicates whether this version is the latest version of the file.
     * Only ONE record per (ownerEmail + originalFilename) should have isLatest = true.
     */
    private boolean isLatest;
}
