package com.vault.secure_vault.security;

import com.vault.secure_vault.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpiration ;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration ;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey getSingingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    //generate token
    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ accessTokenExpiration))
                .signWith(getSingingKey(), Jwts.SIG.HS256)
                .compact();

    }

    public String generateRefreshToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSingingKey(), Jwts.SIG.HS256)
                .compact();

    }

    private Claims extractClaims(String token){
        try{
            return Jwts.parser()
                    .verifyWith(getSingingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (JwtException e){
            throw new IllegalStateException("Invalid JWT token", e);
        }
    }

    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token){
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isValidToken(String token, String email){
        final String username = extractEmail(token);
        return (username.equals(email) && !isTokenExpired(token));
    }
}
