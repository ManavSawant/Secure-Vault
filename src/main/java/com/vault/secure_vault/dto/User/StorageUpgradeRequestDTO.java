package com.vault.secure_vault.dto.User;

import jakarta.validation.constraints.Min;

public record StorageUpgradeRequestDTO(
        @Min(value = 1, message = "Credits must be at least 1") int credits
) {}
