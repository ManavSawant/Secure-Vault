package com.vault.secure_vault.storage.local;
import com.vault.secure_vault.storage.FileStorageService;
import com.vault.secure_vault.util.FileDownloadData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;

@Profile("local")
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    @Value("${storage.upload-dir}")
    private String uploadDir;

    @Override
    public String upload(byte[]data, String storedPath, String contentType) throws IOException {
        try {
            Path baseDir = Paths.get(uploadDir).normalize();
            Files.createDirectories(baseDir);

            Path fullPath = baseDir.resolve(storedPath).normalize();

            // create parent directories if needed (e.g. user folders)
            Files.createDirectories(fullPath.getParent());

            Files.write(fullPath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return storedPath;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file locally", e);
        }
    }

    @Override
    public FileDownloadData download(String storedPath, String originalFilename, String contentType) throws IOException {
        try {
            Path fullPath = Paths.get(uploadDir).resolve(storedPath).normalize();

            if (!Files.exists(fullPath)) {
                throw new RuntimeException("File not found on disk");
            }

            InputStream inputStream = Files.newInputStream(fullPath);

            return new FileDownloadData(
                    inputStream,
                    originalFilename,
                    contentType
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to read file locally", e);
        }
    }

    @Override
    public void delete(String storedPath) {
        try {
            Path fullPath = Paths.get(uploadDir).resolve(storedPath).normalize();
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file locally", e);
        }
    }

}
