package com.vault.secure_vault.repository;

import com.vault.secure_vault.model.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    // List files (UI)
    List<FileMetadata> findByOwnerEmailAndDeletedFalseOrderByCreatedAtDesc(
            String ownerEmail
    );

    // Latest version (upload / replace logic)
    Optional<FileMetadata> findByOwnerEmailAndOriginalFilenameAndDeletedFalseAndIsLatestTrue(
            String ownerEmail,
            String originalFilename
    );

    // Secure access (download / delete)
    Optional<FileMetadata> findByIdAndOwnerEmailAndDeletedFalseAndIsLatestTrue(
            String id,
            String ownerEmail
    );

    // Find previous version (used ONLY during delete)
    Optional<FileMetadata> findTopByOwnerEmailAndOriginalFilenameAndDeletedFalseAndVersionLessThanOrderByVersionDesc(
            String ownerEmail,
            String originalFilename,
            int version
    );

    List<FileMetadata> findByOwnerEmailAndOriginalFilenameOrderByVersionDesc(String ownerEmail, String originalFilename);
    
    Optional<FileMetadata> findByIdAndOwnerEmailAndDeletedTrue(String id, String ownerEmail);

    List<FileMetadata> findByOwnerEmailAndOriginalFilenameAndDeletedFalseOrderByVersionDesc(String ownerEmail, String originalFilename);
}
