package com.vault.secure_vault.service;

import com.vault.secure_vault.config.UploadProperties;
import com.vault.secure_vault.exceptions.FileExceptions.FileTooLargeException;
import com.vault.secure_vault.exceptions.FileExceptions.StorageLimitExceededException;
import com.vault.secure_vault.model.FileMetadata;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.FileMetadataRepository;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.storage.FileStorageService;
import com.vault.secure_vault.util.FileDownloadData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Service responsible for all file-related business logic.
 *
 * <p>This includes:
 * <ul>
 *     <li>File upload with versioning</li>
 *     <li>Storage limit enforcement</li>
 *     <li>Soft delete & restore</li>
 *     <li>File download</li>
 *     <li>Version history retrieval</li>
 * </ul>
 *
 * <p><b>Important:</b> This service enforces ownership and storage rules.
 * Controllers must never bypass this logic.
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private final UserService userService;
    private final FileMetadataRepository repository;
    private final UploadProperties uploadProperties;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    /**
     * Uploads a file for a user with automatic versioning and storage validation.
     *
     * @param file       file to upload
     * @param ownerEmail email of file owner
     * @return saved FileMetadata entity
     * @throws IOException if file read fails
     * @throws IllegalArgumentException if file is empty
     * @throws FileTooLargeException if file exceeds max allowed size
     * @throws StorageLimitExceededException if user storage limit is exceeded
     */
    @Transactional
    public FileMetadata uploadFile(MultipartFile file, String ownerEmail) throws IOException {

        if(file.isEmpty()) throw new IllegalArgumentException("File is empty");


        if(file.getSize() > uploadProperties.getMaxSizeBytes()) throw new FileTooLargeException();

        User user = userService.getByEmail(ownerEmail);

        long usedStorage = user.getStorageUsed();
        long maxAllowedSize = user.getStorageLimit();
        long newFileSize = file.getSize();

        if(usedStorage  + newFileSize > maxAllowedSize) {
            throw new StorageLimitExceededException(
                            "Storage limit exceeded. Used: "+usedStorage+
                            ", File: "+newFileSize+
                            ", Limit: "+maxAllowedSize
            );
        }

        Optional<FileMetadata> latestFileOpt =
                repository.findByOwnerEmailAndOriginalFilenameAndDeletedFalseAndIsLatestTrue(
                        ownerEmail,
                        file.getOriginalFilename()
                );

        int nextVersion = latestFileOpt.map(f -> f.getVersion() + 1).orElse(1);

        latestFileOpt.ifPresent(latestFile -> {
            latestFile.setLatest(false);
            repository.save(latestFile);
        });

         String storedPath = ownerEmail + "/" + UUID.randomUUID()+ "_" + file.getOriginalFilename();
         fileStorageService.upload(
                 file.getBytes(),
                 storedPath,
                 file.getContentType()
         );

        FileMetadata metadata = FileMetadata.builder()
                .ownerEmail(ownerEmail)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedPath)
                .contentType(file.getContentType())
                .size(newFileSize)
                .version(nextVersion)
                .isLatest(true)
                .deleted(false)
                .createdAt(Instant.now())
                .build();

        repository.save(metadata);

        user.addUsedStorage(newFileSize);
        userService.save(user);

        return metadata;
    }

    /**
     * Soft deletes a file (latest version) for a user.
     * Does NOT remove the physical file.
     *
     * @param fileId file ID
     * @param ownerEmail owner email
     */
    @Transactional
    public void softDeleteFile(String fileId, String ownerEmail) {
        FileMetadata file = repository
                .findByIdAndOwnerEmailAndDeletedFalseAndIsLatestTrue(fileId, ownerEmail)
                .orElseThrow(() -> new RuntimeException("File not found"));

        boolean wasLatest = file.isLatest();

        file.setDeleted(true);
        file.setDeletedAt(Instant.now());
        file.setLatest(false);
        repository.save(file);

        User user = userRepository.getByEmail(ownerEmail);
        user.setStorageUsed(user.getStorageUsed() - file.getSize());
        userService.save(user);

        if (wasLatest) {
            repository
                    .findTopByOwnerEmailAndOriginalFilenameAndDeletedFalseAndVersionLessThanOrderByVersionDesc(
                            ownerEmail,
                            file.getOriginalFilename(),
                            file.getVersion()
                    )
                    .ifPresent(prev -> {
                        prev.setLatest(true);
                        repository.save(prev);
                    });
        }
    }

    /**
     * Restores a previously deleted file and marks it as latest version.
     *
     * @param fileId file ID
     * @param ownerEmail owner email
     * @return restored FileMetadata
     */
    @Transactional
    public FileMetadata restoreFile(String fileId, String ownerEmail) {
        FileMetadata file = repository.findByIdAndOwnerEmailAndDeletedTrue(fileId,ownerEmail).orElseThrow(() -> new RuntimeException("File not found"));

        file.setDeleted(false);
        file.setDeletedAt(null);
        file.setLatest(true);

        repository.findByOwnerEmailAndOriginalFilenameAndDeletedFalseOrderByVersionDesc(ownerEmail,file.getOriginalFilename())
                .forEach(f->{
                    if(!f.getId().equals(file.getId())) {
                        f.setLatest(false);
                        repository.save(f);
                    }
                });
        return repository.save(file);
    }

    /**
     * Lists all non-deleted files for a user.
     *
     * @param ownerEmail owner email
     * @return list of FileMetadata
     */
    public List<FileMetadata> listUserFiles(String ownerEmail) {

        return repository.findByOwnerEmailAndDeletedFalseOrderByCreatedAtDesc(ownerEmail);
    }


    public FileMetadata getFileForUser(String ownerEmail, String fileId) {

        FileMetadata file = repository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        if(!file.getOwnerEmail().equals(ownerEmail)){
            throw new RuntimeException("File does not belong to the owner of this file");
        }
        if(file.isDeleted()){
            throw new RuntimeException("File deleted.");
        }

        return file;
    }

    /**
     * Validates that a file belongs to user, is not deleted, and is latest.
     *
     * @param fileId file ID
     * @param ownerEmail owner email
     * @return FileMetadata
     */
    private FileMetadata validateFileAccess(String fileId, String ownerEmail){
        return repository
                .findByIdAndOwnerEmailAndDeletedFalseAndIsLatestTrue(fileId, ownerEmail)
                .orElseThrow(()-> new RuntimeException("file not found "));
    }


    /**
     * Downloads a file for a user after validating access.
     *
     * @param fileId file ID
     * @param ownerEmail owner email
     * @return FileDownloadData containing bytes + metadata
     * @throws IOException if download fails
     */
    public FileDownloadData downloadFile(String fileId, String ownerEmail)  throws IOException {

        FileMetadata file = validateFileAccess(fileId,ownerEmail);
        return fileStorageService.download(
                file.getStoredFilename(),
                file.getOriginalFilename(),
                file.getContentType()
        );
    }

    /**
     * Retrieves all versions of a file for a user.
     *
     * @param fileId base file ID
     * @param ownerEmail owner email
     * @return list of FileMetadata ordered by version desc
     */
    public List<FileMetadata> getFileVersions(String fileId, String ownerEmail) {
        FileMetadata baseFile = repository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        if(!baseFile.getOwnerEmail().equals(ownerEmail)){
            throw new RuntimeException("File does not belong to the owner of this file");
        }

        return repository.findByOwnerEmailAndOriginalFilenameOrderByVersionDesc(
                ownerEmail,
                baseFile.getOriginalFilename()
        );
    }
}
