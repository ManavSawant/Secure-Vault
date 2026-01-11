package com.vault.secure_vault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service responsible for JWT creation, parsing, and validation.
 *
 * <p><b>Security Critical Component</b></p>
 * <p>This class controls how access tokens are generated and verified.
 * Any vulnerability here compromises the entire authentication system.</p>
 */
@Slf4j
@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiry-ms}")
    private long accessTokenExpiration ;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey signingKey;

    /**
     * Initializes and validates the signing key after properties are loaded.
     */
    @PostConstruct
    private void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters long");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT access token for the given user.
     *
     * @param userDetails authenticated user details
     * @return JWT access token string
     */
    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessTokenExpiration * 60_000))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token JWT token
     * @return Claims payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the subject (username/email) from the token.
     *
     * @param token JWT token
     * @return subject value
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates the token against user details and expiry.
     *
     * @param token JWT token
     * @param userDetails user details to validate against
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks whether the token is expired.
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration().before(new Date());
    }

    /**
     * Returns access token TTL in seconds.
     *
     * @return token TTL in seconds
     */
    public long getAccessTokenTtlSeconds() {
        return accessTokenExpiration / 1000;
    }
}
