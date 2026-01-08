package com.vault.secure_vault.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;
    private String token;
    private String userEmail;
    private Instant expiryDate;
    private boolean revoked;
}
