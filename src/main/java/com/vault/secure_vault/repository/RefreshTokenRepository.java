package com.vault.secure_vault.repository;

import com.vault.secure_vault.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    void deleteByUserEmail(String email);
}
