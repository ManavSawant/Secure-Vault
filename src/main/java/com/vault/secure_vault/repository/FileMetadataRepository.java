package com.vault.secure_vault.repository;

import com.vault.secure_vault.model.FileMetadata;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    List<FileMetadata> findByOwnerEmailAndDeletedFalse(String email);

    Optional<FileMetadata> findTopByOwnerEmailAndOriginalFilenameOrderByVersionDesc(String email, String originalFilename);

    Optional<FileMetadata> findByIdAndOwnerEmailAndDeletedFalse(String id,String ownerEmail);
}
