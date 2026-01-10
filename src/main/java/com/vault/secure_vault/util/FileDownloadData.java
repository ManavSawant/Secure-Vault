package com.vault.secure_vault.util;

import org.springframework.core.io.Resource;

import java.io.InputStream;


public record FileDownloadData(
        InputStream inputStream,
        String originalFilename,
        String contentType
) {}
