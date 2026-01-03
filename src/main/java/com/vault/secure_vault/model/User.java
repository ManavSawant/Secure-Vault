package com.vault.secure_vault.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user_profiles")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private String photoUrl;

    private int credits;
    private long storageUsed;
    private long storageLimit;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean isDeleted;

    // Increment storage
    public void addUsedStorage(long bytes) {
        this.storageUsed += bytes;
    }

    // Decrement storage
    public void removeUsedStorage(long bytes) {
        this.storageUsed -= bytes;
        if(this.storageUsed < 0) this.storageUsed = 0;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
