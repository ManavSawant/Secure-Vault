package com.vault.secure_vault.exceptions;

import com.vault.secure_vault.dto.Error.ErrorResponse;
import lombok.Builder;

@Builder
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorResponse error
) {
    public static <T>ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static ApiResponse<?> failure(ErrorResponse error) {
        return ApiResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}
