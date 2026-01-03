package com.vault.secure_vault.exceptions.FileExceptions;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException() {
        super("File size exceeds allowed limit");
    }
}
