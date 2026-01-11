package com.vault.secure_vault.controller;

import com.vault.secure_vault.dto.User.*;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user profile, storage, and credit related operations.
 */
@Tag(name = "User", description = "User related APIs")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user in the system.
     *
     * @param request User registration details (name, email, password)
     * @return AuthResponseDTO containing access and refresh tokens
     */
    @Operation(
            summary = "Register user",
            description = "Creates a new user account and returns JWT tokens"
    )
   @PostMapping("/register")
    public ResponseEntity<@NotNull UserResponseDTO> register(
            @Valid @RequestBody UserRegistrationRequestDTO request
   ){
       User user = userService.registerUser(request);
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(mapUserToResponse(user));
   }

    /**
     * Returns the profile of the logged-in user.
     *
     * @param authentication authenticated user context
     * @return user profile details
     */
    @Operation(
            summary = "Get user profile",
            description = "Returns profile information of the logged-in user"
    )
    @GetMapping("/me")
    public ResponseEntity<@NotNull UserResponseDTO> getCurrentUser(
            Authentication authentication
   ){
       User user = userService.getCurrentUser(authentication.getName());
       return ResponseEntity.ok(mapUserToResponse(user));
   }

    /**
     * Updates name/email of logged-in user.
     *
     * @param request update request DTO
     * @param authentication authenticated user context
     * @return updated profile
     */
    @Operation(
            summary = "Update user profile",
            description = "Updates name and email of the logged-in user"
    )
    @PutMapping("/me")
   public ResponseEntity<@NotNull UserResponseDTO> updateProfile(
           Authentication authentication,
           @RequestBody @Valid UserProfileUpdateDTO request
   ){
       User user = userService.updateProfile(authentication.getName(), request);
       return ResponseEntity.ok(mapUserToResponse(user));
   }

    /**
     * Spends user credits.
     *
     * @param request spend credits request
     * @param authentication authenticated user context
     */
    @Operation(
            summary = "Spend credits",
            description = "Spends credits from user's account"
    )
    @PostMapping("/me/credits/spend")
    public UserResponseDTO spendCredits(
            Authentication authentication,
            @Valid @RequestBody SpendCreditsRequestDTO request
   ){
       User user = userService.spendCreditsForStorage(authentication.getName(), request.credits());
       return mapUserToResponse(user);
   }

    /**
     * Upgrades storage by spending credits.
     *
     * @param request storage upgrade request
     * @param authentication authenticated user context
     * @return upgrade result
     */
    @Operation(
            summary = "Upgrade storage",
            description = "Upgrades user storage by spending credits"
    )
    @PostMapping("/me/storage/upgrade")
    public ResponseEntity<@NotNull StorageUpgradeResponseDTO> upgradeStorage(
            @Valid @RequestBody StorageUpgradeRequestDTO request,
            Authentication authentication
    ){
        User user = userService.upgradeStorage(authentication.getName(), request);

        StorageUpgradeResponseDTO response = StorageUpgradeResponseDTO.builder()
                .newStorageLimit(user.getStorageLimit())
                .remainingCredits(user.getCredits())
                .build();

        return ResponseEntity.ok(response);
    }





    private UserResponseDTO mapUserToResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .photoUrl(user.getPhotoUrl())
                .credits((user.getCredits()))
                .storageUsed(user.getStorageUsed())
                .storageLimit(user.getStorageLimit())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
