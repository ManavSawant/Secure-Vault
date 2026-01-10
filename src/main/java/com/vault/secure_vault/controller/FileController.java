package com.vault.secure_vault.controller;

import com.vault.secure_vault.config.OpenApiConfig;
import com.vault.secure_vault.dto.File.FileRestoreResponseDTO;
import com.vault.secure_vault.dto.File.FileUploadResponseDTO;
import com.vault.secure_vault.dto.File.FileVersionResponseDTO;
import com.vault.secure_vault.model.FileMetadata;
import com.vault.secure_vault.service.FileService;
import com.vault.secure_vault.util.FileDownloadData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Tag(name = "Files", description = "File management APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Operation(
            summary = "Upload file",
            description = "Uploads a file and creates a new version if it already exists",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NotNull FileMetadata> fileUpload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        return ResponseEntity.ok(fileService.uploadFile(file, authentication.getName()));
    }


    @Operation(
            summary = "List user files",
            description = "Returns all non-deleted files for the logged-in user",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @GetMapping
    public ResponseEntity<@NotNull List<FileUploadResponseDTO>>listOfFiles(Authentication authentication) {

        List<FileUploadResponseDTO> files =
                fileService.listUserFiles(authentication.getName())
                        .stream()
                        .map(this::mapToUploadResponse)
                        .toList();
        return ResponseEntity.ok(files);
    }

    @Operation(
            summary = "Download file",
            description = "Downloads the latest version of a file",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
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
                .contentType(MediaType.parseMediaType(data.contentType()))
                .body(new InputStreamResource(data.inputStream()));
    }

    @Operation(
            summary = "Delete file",
            description = "Soft deletes a file",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @DeleteMapping("/{fileId}")
    public ResponseEntity<@NotNull Void> softDelete(
            @PathVariable String fileId,
            Authentication authentication
    ) {
        fileService.softDeleteFile(fileId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{fileId}/restore")
    public ResponseEntity<FileRestoreResponseDTO> restoreFile(
            @PathVariable String fileId,
            Authentication authentication
    ) {
        FileMetadata file = fileService.restoreFile(fileId, authentication.getName());

        FileRestoreResponseDTO response = FileRestoreResponseDTO.builder()
                .fileId(file.getId())
                .fileName(file.getOriginalFilename())
                .version(file.getVersion())
                .restored(true)
                .restoredAt(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Get file versions",
            description = "Returns all versions of a file",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
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