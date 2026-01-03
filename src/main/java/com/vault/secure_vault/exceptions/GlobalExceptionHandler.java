package com.vault.secure_vault.exceptions;

import com.mongodb.DuplicateKeyException;
import com.vault.secure_vault.exceptions.FileExceptions.FileTooLargeException;
import com.vault.secure_vault.exceptions.FileExceptions.InvalidFileTypeExceptions;
import com.vault.secure_vault.exceptions.FileExceptions.StorageLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<?> handleDuplicateEmailException(DuplicateKeyException ex){
        Map<String , Object> data = new HashMap<>();
        data.put("status", HttpStatus.CONFLICT);
        data.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(data);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<String> handleFileTooLarge(FileTooLargeException e){
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(e.getMessage());
    }

    @ExceptionHandler(InvalidFileTypeExceptions.class)
    public ResponseEntity<String> handleInvalidFileType(InvalidFileTypeExceptions e){
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
    }

    @ExceptionHandler(StorageLimitExceededException.class)
    public ResponseEntity<String> handleStorageLimitExceeded(StorageLimitExceededException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
