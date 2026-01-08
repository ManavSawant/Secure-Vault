package com.vault.secure_vault.dto.User;

import lombok.Builder;

@Builder
public record StorageUpgradeResponseDTO(
        int remainingCredits,
        long newStorageLimit
) { }
