package com.vault.secure_vault.Auth;


import lombok.Builder;

@Builder
public record AuthResponseDTO(
         String accessToken,
         String refreshToken,
         String tokenType,
         long expiresIn) {

}
