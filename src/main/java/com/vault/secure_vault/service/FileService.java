package com.vault.secure_vault.service;

import com.vault.secure_vault.dto.File.FileUploadResponseDTO;
import com.vault.secure_vault.model.FileMetadata;
import com.vault.secure_vault.repository.FileMetadataRepository;
import com.vault.secure_vault.util.FileDownloadData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository repository;

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

    public FileUploadResponseDTO uploadFile(MultipartFile file, String ownerEmail) throws IOException {

        int nextVersion = repository
                .findTopByOwnerEmailAndOriginalFilenameOrderByVersionDesc(ownerEmail,file.getOriginalFilename())
                .map(m -> m.getVersion() + 1).orElse(1);

        String storedFilename = UUID.randomUUID() + "_" +  file.getOriginalFilename();

        File dir = new File(uploadDirectory);
        if(!dir.exists() && !dir.mkdirs()){
            throw new IOException("Could not create directory");
        }

        File destination = new File(dir , storedFilename);
        file.transferTo(destination);

        FileMetadata metadata = FileMetadata.builder()
                .ownerEmail(ownerEmail)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .contentType(file.getContentType())
                .size(file.getSize())
                .version(nextVersion)
                .deleted(false)
                .createdAt(Instant.now())
                .build();

        FileMetadata savedMetadata = repository.save(metadata);

        return mapToDTO(savedMetadata);
    }

    public List<FileUploadResponseDTO> listUserFiles(String ownerEmail) {

        return repository.findByOwnerEmailAndDeletedFalse(ownerEmail)
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

    private FileMetadata validateFileAccess(String filedId,String ownerEmail){

        FileMetadata file = repository.findById(filedId).orElseThrow(() -> new RuntimeException("File not found"));

        if(file.isDeleted()){
            throw new RuntimeException("File deleted.");
        }

        if(!file.getOwnerEmail().equals(ownerEmail)){
            throw new RuntimeException("Access denied.");
        }
        return file;
    }
    public FileDownloadData downloadFile(String filedId, String ownerEmail) throws IOException {

        FileMetadata file = validateFileAccess(filedId,ownerEmail);

        Path filePath = Paths.get(uploadDirectory)
                .resolve(file.getStoredFilename())
                .normalize();

        try{
            Resource resource = new UrlResource(filePath.toUri());

            if(!resource.exists() || !resource.isReadable()){
                throw new IOException("File not found on disk");
            }
            return new FileDownloadData(resource, file.getOriginalFilename());
        }catch (MalformedURLException e){
            throw new IOException("invalid file path", e);
        }
    }

}
