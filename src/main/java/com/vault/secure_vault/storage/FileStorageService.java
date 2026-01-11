package com.vault.secure_vault.storage;

import com.vault.secure_vault.util.FileDownloadData;
import java.io.IOException;

/**
 * Abstraction for file storage operations.
 *
 * <p>This interface decouples the application from the actual storage provider
 * (local filesystem, AWS S3, etc.). Business logic must never depend on a specific
 * storage implementation.</p>
 *
 * <p>Implementations are selected using Spring profiles.</p>
 */
public interface FileStorageService {
    /**
     * Uploads file content to the storage backend.
     *
     * @param data        raw file bytes
     * @param path        logical storage path (must be unique)
     * @param contentType MIME type of the file
     * @throws IOException if upload fails
     */
    String upload(byte[] data, String path, String contentType) throws IOException;

    /**
     * Downloads a file from storage.
     *
     * @param storedPath      internal storage path
     * @param originalFilename    original filename for download
     * @param contentType     MIME type
     * @return FileDownloadData containing bytes + metadata
     * @throws IOException if file not found or read fails
     */
    FileDownloadData download(String storedPath, String originalFilename,String contentType) throws IOException;

    /**
     * Deletes a file from storage backend.
     *
     * <p>Note: This is physical deletion. Soft delete is handled at metadata level.</p>
     *
     * @param storedPath internal storage path
     * @throws IOException if deletion fails
     */
    void delete(String storedPath);
}
