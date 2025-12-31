package com.vault.secure_vault.controller;

import com.vault.secure_vault.dto.File.FileUploadResponseDTO;
import com.vault.secure_vault.service.FileService;
import com.vault.secure_vault.util.FileDownloadData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    public FileUploadResponseDTO fileUpload(@RequestParam("file") MultipartFile file, Authentication authentication) throws Exception {
        return fileService.uploadFile(file, authentication.getName());
    }

    public List<FileUploadResponseDTO> listOfFiles(Authentication authentication) {
        return fileService.listUserFiles(authentication.getName());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, Authentication authentication) throws IOException {
        FileDownloadData data = fileService.downloadFile(fileId, authentication.getName());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\" "+ data.originalFilename() + "\""
                )
                .body(data.resource());
    }


}
