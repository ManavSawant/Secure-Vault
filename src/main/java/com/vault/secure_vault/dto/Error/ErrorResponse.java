package com.vault.secure_vault.dto.Error;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        Instant timeStamp,
        int status,
        String error,
        String message,
        String path
) {
}
