package com.vault.secure_vault.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Set;

/**
 * Configuration properties for file upload constraints.
 * <p>
 * Binds values from application properties with prefix {@code app.upload}.
 * Used to control maximum file size and allowed file types.
 */
@Configuration
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class UploadProperties {

    /**
     * Maximum allowed file size in bytes.
     */
    private long maxSizeBytes;
    /**
     * Set of allowed MIME types for uploaded files.
     * Example: image/png, application/pdf
     */
    private Set<String> allowedFileTypes;
}
