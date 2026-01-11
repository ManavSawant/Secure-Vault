package com.vault.secure_vault.exceptions;

import com.vault.secure_vault.dto.Error.ErrorResponse;
import com.vault.secure_vault.exceptions.FileExceptions.FileTooLargeException;
import com.vault.secure_vault.exceptions.FileExceptions.InvalidFileTypeExceptions;
import com.vault.secure_vault.exceptions.FileExceptions.StorageLimitExceededException;
import com.vault.secure_vault.exceptions.User.InsufficientCreditsException;
import com.vault.secure_vault.exceptions.User.UserAlreadyExistsException;
import com.vault.secure_vault.exceptions.User.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ApiResponse<?>> build(
            HttpStatus status,
            String errorCode,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(status.value())
                .error(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(ApiResponse.failure(response));
    }

    // ============================
    // File & Storage
    // ============================

    @ExceptionHandler(StorageLimitExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleStorageLimitExceeded(
            StorageLimitExceededException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "STORAGE_LIMIT_EXCEEDED",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ApiResponse<?>> handleFileTooLarge(
            FileTooLargeException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "FILE_TOO_LARGE",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidFileTypeExceptions.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidFileType(
            InvalidFileTypeExceptions ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "INVALID_FILE_TYPE",
                ex.getMessage(),
                request
        );
    }

    // ============================
    // Authentication & Authorization
    // ============================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_FAILED",
                "Invalid email or password",
                request
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUsernameNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "USER_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                request
        );
    }

    // ============================
    // JWT
    // ============================

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleExpiredJwt(
            ExpiredJwtException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "TOKEN_EXPIRED",
                "Access token has expired",
                request
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(
            JwtException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "INVALID_TOKEN",
                "Invalid or malformed JWT token",
                request
        );
    }

    // ============================
    // Validation
    // ============================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_JSON",
                "Request body is invalid or malformed",
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "TYPE_MISMATCH",
                "Invalid parameter type: " + ex.getName(),
                request
        );
    }

    // ============================
    // User & Business
    // ============================

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "USER_ALREADY_EXISTS",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InsufficientCreditsException.class)
    public ResponseEntity<ApiResponse<?>> handleInsufficientCredits(
            InsufficientCreditsException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "INSUFFICIENT_CREDITS",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "INVALID_STATE",
                ex.getMessage(),
                request
        );
    }

    // ============================
    // Fallback
    // ============================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception occurred", ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request
        );
    }
}
