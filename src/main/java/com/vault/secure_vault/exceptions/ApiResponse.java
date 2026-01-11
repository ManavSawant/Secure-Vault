package com.vault.secure_vault.exceptions;

import com.vault.secure_vault.dto.Error.ErrorResponse;
import lombok.Builder;

/**
 * Standard API response wrapper used across the entire application.
 * <p>
 * This class enforces a consistent response contract for both successful
 * and failed API responses.
 *
 * <pre>
 * Success response:
 * {
 *   "success": true,
 *   "data": { ... },
 *   "error": null
 * }
 *
 * Error response:
 * {
 *   "success": false,
 *   "data": null,
 *   "error": {
 *     "timeStamp": "...",
 *     "status": 400,
 *     "error": "VALIDATION_ERROR",
 *     "message": "Email is required",
 *     "path": "/api/auth/register"
 *   }
 * }
 * </pre>
 *
 * @param <T> the type of the response data payload
 */
@Builder
public record ApiResponse<T>(

        boolean success,
        T data,
        ErrorResponse error
) {

    /**
     * Creates a successful API response wrapper.
     *
     * @param data the response payload
     * @param <T>  the type of the response payload
     * @return a successful {@link ApiResponse} containing the given data
     */
    public static <T>ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Creates a failed API response wrapper.
     *
     * @param error the error details
     * @return a failed {@link ApiResponse} containing the given error information
     */
    public static ApiResponse<?> failure(ErrorResponse error) {
        return ApiResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}
