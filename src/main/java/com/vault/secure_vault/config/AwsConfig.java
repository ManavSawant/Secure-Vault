package com.vault.secure_vault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


/**
 * AWS configuration for Secure Vault.
 * <p>
 * This configuration is only active when the {@code cloud} profile is enabled.
 * It sets up the AWS S3 client using access key and secret key.
 */
@Configuration
@Profile("cloud")
public class AwsConfig {

    /**
     * AWS region (e.g. ap-south-1).
     */
    @Value("${aws.region}")
    private String region;

    /**
     * AWS access key for authentication.
     */
    @Value("${aws.access-key}")
    private String accessKey;

    /**
     * AWS secret key for authentication.
     */
    @Value("${aws.secret-key}")
    private String secretKey;

    /**
     * Creates an {@link S3Client} bean for interacting with AWS S3.
     *
     * @return configured {@link S3Client}
     */
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
