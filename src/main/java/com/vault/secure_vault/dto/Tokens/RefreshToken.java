package com.vault.secure_vault.dto.Tokens;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_token")
public class RefreshToken {
    @Id
    private String id;

    private String userEmail;

    private String tokenHash;

    private Instant expiryDate;

    private boolean revoked;
}
