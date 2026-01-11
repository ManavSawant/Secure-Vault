package com.vault.secure_vault.dto.Auth;


import lombok.Builder;

/**
 * Response returned after successful authentication.
 * Contains access token, refresh token and token metadata.
 */
@Builder
public record AuthResponseDTO(
         String accessToken,
         String refreshToken,
         String tokenType,
         long expiresIn) {

}
