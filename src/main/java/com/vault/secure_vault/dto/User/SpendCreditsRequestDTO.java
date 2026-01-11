package com.vault.secure_vault.dto.User;

import jakarta.validation.constraints.Min;

/**
 * Request DTO for spending credits.
 * Currently used for storage upgrades.
 */
public record SpendCreditsRequestDTO(
        @Min(1)
        int credits
) {}
