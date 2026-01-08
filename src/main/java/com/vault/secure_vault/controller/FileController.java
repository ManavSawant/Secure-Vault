package com.vault.secure_vault.controller;

import com.vault.secure_vault.dto.File.FileUploadResponseDTO;
import com.vault.secure_vault.dto.File.FileVersionResponseDTO;
import com.vault.secure_vault.model.FileMetadata;
import com.vault.secure_vault.service.FileService;
import com.vault.secure_vault.util.FileDownloadData;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<@NotNull FileUploadResponseDTO> fileUpload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws Exception {

        FileMetadata metadata = fileService.uploadFile(file, authentication.getName());

        return ResponseEntity.ok(mapToUploadResponse(metadata));
    }

    @GetMapping
    public ResponseEntity<@NotNull List<FileUploadResponseDTO>>listOfFiles(Authentication authentication) {

        List<FileUploadResponseDTO> files =
                fileService.listUserFiles(authentication.getName())
                .stream()
                .map(this::mapToUploadResponse)
                .toList();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<@NotNull Resource> downloadFile(
            @PathVariable String fileId,
            Authentication authentication
    ) throws IOException {

        FileDownloadData data =
                fileService.downloadFile(fileId, authentication.getName());

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + data.originalFilename() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data.resource());
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<@NotNull Void> softDelete(
            @PathVariable String fileId,
            Authentication authentication
    ) {
        fileService.softDeleteFile(fileId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{fileId}/version")
    public ResponseEntity<@NotNull List<FileVersionResponseDTO>>getFileVersions(
            @PathVariable String fileId,
            Authentication authentication
    ) {
        List<FileVersionResponseDTO> version =
                fileService.getFileVersions(fileId, authentication.getName())
                        .stream()
                        .map(this::mapToVersionResponse)
                        .toList();
        return ResponseEntity.ok(version);
    }
    private FileUploadResponseDTO mapToUploadResponse(FileMetadata file) {
        return FileUploadResponseDTO.builder()
                .fileId(file.getId())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .updatedAt(file.getCreatedAt())
                .build();
    }

    private FileVersionResponseDTO mapToVersionResponse(FileMetadata file) {
        return FileVersionResponseDTO.builder()
                .fileId(file.getId())
                .version(file.getVersion())
                .size(file.getSize())
                .isLatest(file.isLatest())
                .deleted(file.isDeleted())
                .createdAt(file.getCreatedAt())
                .build();
    }

}
