package com.vault.secure_vault.Auth;

import com.vault.secure_vault.model.RefreshToken;

public record AuthResult(
        String accessToken,
        RefreshToken refreshToken,
        long expiration
) {}
