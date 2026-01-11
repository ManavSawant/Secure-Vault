package com.vault.secure_vault.dto.User;

import lombok.Builder;

/**
 * Response DTO returned after successful storage upgrade.
 */
@Builder
public record StorageUpgradeResponseDTO(
        int remainingCredits,
        long newStorageLimit
) {}
