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
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<@NotNull ApiResponse<?>> build(HttpStatus status,
                                                          String error,
                                                          String message,
                                                          HttpServletRequest request
    ){
        ErrorResponse response = ErrorResponse.builder()
                .timeStamp(Instant.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(ApiResponse.failure(response));
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    public ResponseEntity<@NotNull ApiResponse<?>> handleStorageLimit(StorageLimitExceededException ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "STORAGE_LIMIT_EXCEEDED",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<@NotNull ApiResponse<?>> handleFileTooLarge(FileTooLargeException ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "FILE_TOO_LARGE",
                "Uploaded file exceeds max allowed size",
                request
        );
    }

    @ExceptionHandler(InvalidFileTypeExceptions.class)
    public ResponseEntity<@NotNull ApiResponse<?>> handleInvalidFileType(InvalidFileTypeExceptions ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "INVALID_FILE_TYPE",
                "Invalid file type is not allowed",
                request
        );
    }
    //authentication
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthentication(AuthenticationException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Invalid user name password",
                request
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "USER_NOT_FOUND",
                "User does not exist",
                request
        );
    }

    //authorization
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request){
        return build(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You don't have permission to access this data",
                request
        );
    }

    //JWT
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "TOKEN_EXPIRED",
                "Access token expired",
                request
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "INVALID_TOKEN",
                "Invalid or malformed token",
                request
        );
    }

    //validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request){
        String message = ex.getBindingResult()
                .getFieldErrors().stream().map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message,
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<@NotNull ApiResponse<?>> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request){
        return build(
                HttpStatus.CONFLICT,
                "INVALID_STATE",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<@NotNull ApiResponse<?>> handleUserAlreadyExists(
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
    public ResponseEntity<@NotNull ApiResponse<?>> handleUserNotFound(
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
    public ResponseEntity<@NotNull ApiResponse<?>> handleInsufficientCredits(
            InsufficientCreditsException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_CREDITS",
                ex.getMessage(),
                request
        );
    }

    //fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NotNull ApiResponse<?>> handleGenric(Exception ex, HttpServletRequest request){

        log.error("Unhandled exception", ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Something went wrong",
                request
        );
    }

}
