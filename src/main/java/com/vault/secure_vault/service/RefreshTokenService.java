package com.vault.secure_vault.service;

import com.vault.secure_vault.model.RefreshToken;
import com.vault.secure_vault.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service responsible for managing refresh tokens lifecycle.
 *
 * <p>This includes:
 * <ul>
 *     <li>Creating new refresh tokens</li>
 *     <li>Validating existing tokens</li>
 *     <li>Revoking tokens</li>
 * </ul>
 *
 * <p><b>Security Critical:</b> This service directly controls authentication sessions.
 * Any change here affects login, logout, and token refresh behavior.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long SECONDS_IN_DAY = 86400L;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Creates a new refresh token for a user.
     * Any existing refresh token for the user is revoked before creation.
     *
     * @param userEmail email of the user
     * @param expiryDays number of days before token expiry
     * @return newly created RefreshToken
     */
    @Transactional
    public RefreshToken create(String userEmail, int expiryDays){
        refreshTokenRepository.deleteByUserEmail(userEmail);

        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .userEmail(userEmail)
                        .expiryDate(Instant.now().plusSeconds(expiryDays * SECONDS_IN_DAY))
                        .revoked(false)
                        .build()
        );
    }

    /**
     * Validates a refresh token.
     *
     * <p>Checks:
     * <ul>
     *     <li>Token exists</li>
     *     <li>Token is not revoked</li>
     *     <li>Token is not expired</li>
     * </ul>
     *
     * @param token refresh token string
     * @return valid RefreshToken entity
     * @throws IllegalStateException if token is invalid or expired
     */
    public RefreshToken validateToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(token).orElseThrow(()-> new RuntimeException("Invalid refresh token"));

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    /**
     * Revokes a refresh token, making it unusable for future authentication.
     *
     * @param token RefreshToken entity to revoke
     */
    public void revokeToken(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

}
