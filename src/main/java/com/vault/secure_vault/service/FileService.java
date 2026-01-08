package com.vault.secure_vault.service;

import com.vault.secure_vault.config.UploadProperties;
import com.vault.secure_vault.dto.File.FileUploadResponseDTO;
import com.vault.secure_vault.dto.File.FileVersionResponseDTO;
import com.vault.secure_vault.dto.User.UserResponseDTO;
import com.vault.secure_vault.exceptions.FileExceptions.FileTooLargeException;
import com.vault.secure_vault.exceptions.FileExceptions.InvalidFileTypeExceptions;
import com.vault.secure_vault.exceptions.FileExceptions.StorageLimitExceededException;
import com.vault.secure_vault.model.FileMetadata;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.FileMetadataRepository;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.util.FileDownloadData;
import com.vault.secure_vault.util.constant.StorageConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserService userService;
    private final FileMetadataRepository repository;
    private final UploadProperties uploadProperties;
    private final UserRepository userRepository;

    @Value("${storage.upload-dir}")
    private String uploadDirectory;

    private FileUploadResponseDTO mapToDTO(FileMetadata file) {
        return FileUploadResponseDTO.builder()
                .fileId(file.getId())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .updatedAt(file.getCreatedAt())
                .build();
    }

    @Transactional
    public FileUploadResponseDTO uploadFile(MultipartFile file, String ownerEmail) throws IOException {

        if(file.isEmpty()) throw new IllegalArgumentException("File is empty");


        if(file.getSize() > uploadProperties.getMaxSizeBytes()) throw new FileTooLargeException();

        User user = userService.getByEmail(ownerEmail);
        long usedStorage = user.getStorageUsed();
        long maxAllowedSize = user.getStorageLimit();
        long newFileSize = file.getSize();

        if(usedStorage  + newFileSize > maxAllowedSize) {
            throw new StorageLimitExceededException("Storage limit exceeded. Used: "+usedStorage+", File: "+newFileSize+", Limit: "+maxAllowedSize);
        }

        Optional<FileMetadata> latestFileOpt = repository.findByOwnerEmailAndOriginalFilenameAndDeletedFalseAndIsLatestTrue(ownerEmail,file.getOriginalFilename());

        int nextVersion = latestFileOpt.map(f -> f.getVersion() + 1).orElse(1);

        latestFileOpt.ifPresent(latestFile -> {
            latestFile.setLatest(false);
            repository.save(latestFile);
        });

         String storedFilename = UUID.randomUUID() + "_" +  file.getOriginalFilename();
         Path uploadPath = Paths.get(uploadDirectory).normalize();
         Files.createDirectories(uploadPath);
         Path destination = uploadPath.resolve(storedFilename);
         Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        FileMetadata metadata = FileMetadata.builder()
                .ownerEmail(ownerEmail)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
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

        return mapToDTO(metadata);
    }

    @Transactional
    public void softDeleteFile(String fileId, String ownerEmail) {
        FileMetadata file = repository.findByIdAndOwnerEmailAndDeletedFalseAndIsLatestTrue(fileId,ownerEmail).orElseThrow(()-> new RuntimeException("File not found"));

        boolean wasLastest = file.isLatest();

        file.setDeleted(true);
        file.setDeletedAt(Instant.now());
        file.setLatest(false);
        repository.save(file);

        User user = userRepository.getByEmail(ownerEmail);
        user.setStorageUsed(user.getStorageUsed() - file.getSize());
        userService.save(user);

        if(wasLastest) {
            repository.findTopByOwnerEmailAndOriginalFilenameAndDeletedFalseAndVersionLessThanOrderByVersionDesc(
                    ownerEmail,
                    file.getOriginalFilename(),
                    file.getVersion()
            ).ifPresent(prev->{
                prev.setLatest(true);
                repository.save(prev);
            });
        }
    }

    public List<FileUploadResponseDTO> listUserFiles(String ownerEmail) {

        return repository.findByOwnerEmailAndDeletedFalseOrderByCreatedAtDesc(ownerEmail)
                .stream()
                .map(this::mapToDTO)
                .toList();
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

    private FileMetadata validateFileAccess(String filedId, String ownerEmail){
        return repository
                .findByIdAndOwnerEmailAndDeletedFalseAndIsLatestTrue(filedId, ownerEmail)
                .orElseThrow(()-> new RuntimeException("file not found "));
    }


    public FileDownloadData downloadFile(String filedId, String ownerEmail)  throws IOException {

        FileMetadata file = validateFileAccess(filedId,ownerEmail);

        Path filePath = Paths.get(uploadDirectory)
                .resolve(file.getStoredFilename())
                .normalize();

        try{
            Resource resource = new UrlResource(filePath.toUri());
            if(!resource.exists() || !resource.isReadable()) throw new IOException("File not found on disk");
            return new FileDownloadData(resource, file.getOriginalFilename());
        }catch (MalformedURLException e){
            throw new IOException("invalid file path", e);
        }
    }

    public List<FileVersionResponseDTO> getFileVersions(String filedId, String ownerEmail) {
        FileMetadata baseFile = repository.findById(filedId).orElseThrow(() -> new RuntimeException("File not found"));

        if(!baseFile.getOwnerEmail().equals(ownerEmail)){
            throw new RuntimeException("File does not belong to the owner of this file");
        }

        return repository.findByOwnerEmailAndOriginalFilenameOrderByVersionDesc(ownerEmail, baseFile.getOriginalFilename())
                .stream()
                .map(file -> FileVersionResponseDTO.builder()
                        .fileId(file.getId())
                        .version(file.getVersion())
                        .size(file.getSize())
                        .isLatest(file.isLatest())
                        .deleted(file.isDeleted())
                        .createdAt(file.getCreatedAt())
                        .build()
                )
                .toList();
    }
}
