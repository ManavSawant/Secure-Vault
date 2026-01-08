package com.vault.secure_vault.dto.User;

import jakarta.validation.constraints.Min;

public record StorageUpgradeRequestDTO(
        @Min(1) int credits
) { }
