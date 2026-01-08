package com.vault.secure_vault.exceptions;

import com.vault.secure_vault.dto.Error.ErrorResponse;
import com.vault.secure_vault.exceptions.FileExceptions.FileTooLargeException;
import com.vault.secure_vault.exceptions.FileExceptions.InvalidFileTypeExceptions;
import com.vault.secure_vault.exceptions.FileExceptions.StorageLimitExceededException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String error,
                                                String message,
                                                HttpServletRequest request
    ){
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .timeStamp(Instant.now())
                        .status(status.value())
                        .error(error)
                        .message(message)
                        .path(request.getRequestURI())
                        .build()

        );
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleStorageLimit(StorageLimitExceededException ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "STORAGE_LIMIT_EXCEEDED",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(FileTooLargeException ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "FILE_TOO_LARGE",
                "Uploaded file exceeds max allowed size",
                request
        );
    }

    @ExceptionHandler(InvalidFileTypeExceptions.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileType(InvalidFileTypeExceptions ex, HttpServletRequest request){
        return build(
                HttpStatus.BAD_REQUEST,
                "INVALID_FILE_TYPE",
                "Invalid file type is not allowed",
                request
        );
    }
    //authentication
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Invalid user name password",
                request
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "USER_NOT_FOUND",
                "User does not exist",
                request
        );
    }

    //authorization
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request){
        return build(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You don't have permission to access this data",
                request
        );
    }

    //JWT
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "TOKEN_EXPIRED",
                "Access token expired",
                request
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex, HttpServletRequest request){
        return build(
                HttpStatus.UNAUTHORIZED,
                "INVALID_TOKEN",
                "Invalid or malformed token",
                request
        );
    }

    //validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request){
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

    //fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenric(Exception ex, HttpServletRequest request){

        log.error("Unhandled exception", ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Something went wrong",
                request
        );
    }

}
