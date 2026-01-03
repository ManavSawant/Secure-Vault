package com.vault.secure_vault.exceptions.FileExceptions;

public class StorageLimitExceededException extends RuntimeException {
    public StorageLimitExceededException(String s) {
        super("Storage limit exceeded. Upgrade your plane");
    }
}
