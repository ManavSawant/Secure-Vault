package com.vault.secure_vault.service;

import com.vault.secure_vault.model.RefreshToken;
import com.vault.secure_vault.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken create(String userEmail, int expiryDays){
        refreshTokenRepository.deleteByUserEmail(userEmail);

        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .userEmail(userEmail)
                        .expiryDate(Instant.now().plusSeconds(expiryDays * 86400L))
                        .revoked(false)
                        .build()
        );
    }

    public RefreshToken validateToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(token).orElseThrow(()-> new RuntimeException("Invalid refresh token"));

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeToken(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

}
