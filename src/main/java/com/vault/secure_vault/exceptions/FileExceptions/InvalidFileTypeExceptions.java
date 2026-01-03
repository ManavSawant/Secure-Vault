package com.vault.secure_vault.exceptions.FileExceptions;

public class InvalidFileTypeExceptions extends RuntimeException {
    public InvalidFileTypeExceptions() {
        super("unsupported file type");
    }
}
