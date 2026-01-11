package com.vault.secure_vault.dto.Error;

import com.vault.secure_vault.exceptions.ApiResponse;
import lombok.Builder;

import java.time.Instant;

/**
 * Standard error response structure returned by the API when an exception occurs.
 * <p>
 * This object is wrapped inside {@link ApiResponse} when a request fails.
 * It provides detailed information about the error for debugging and client handling.
 */
@Builder
public record ErrorResponse(
        Instant timeStamp,
        int status,
        String error,
        String message,
        String path
) {}
