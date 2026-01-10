package com.vault.secure_vault.storage;

import com.vault.secure_vault.util.FileDownloadData;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


public interface FileStorageService {

    String upload(byte[] data, String storedPath, String contentType) throws IOException;

    FileDownloadData download(String storedPath, String originalFilename,String contentType) throws IOException;

    void delete(String storedPath);
}
