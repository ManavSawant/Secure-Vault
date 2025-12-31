package com.vault.secure_vault.util;

import org.springframework.core.io.Resource;

public record FileDownloadData(Resource resource, String originalFilename) {
}
