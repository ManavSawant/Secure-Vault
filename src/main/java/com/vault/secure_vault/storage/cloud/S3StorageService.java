package com.vault.secure_vault.storage.cloud;

import com.vault.secure_vault.storage.FileStorageService;
import com.vault.secure_vault.util.FileDownloadData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.IOException;

/**
 * AWS S3 implementation of {@link FileStorageService}.
 *
 * <p>Used in production environment. Stores files in S3 bucket.</p>
 */
@Service
@Profile("cloud")
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
@RequiredArgsConstructor
public class S3StorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String upload(byte[] data, String storedPath,String contentType) throws IOException {
        try{
            //upload = putObject
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedPath)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(data)
            );
            return storedPath;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to s3", e);
        }
    }

    @Override
    public FileDownloadData download(String storedPath,String originalFilename,String contentType) throws IOException {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedPath)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object =
                    s3Client.getObject(request);

            return new FileDownloadData(
                    s3Object,
                    originalFilename,
                    contentType
            );

        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from S3", e);
        }
    }

    @Override
    public void delete(String storedPath) {
        try{
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedPath)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e){
            throw new RuntimeException("failed to delete file from s3", e);
        }
    }
}
