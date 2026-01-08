package com.vault.secure_vault.controller;

import com.vault.secure_vault.config.OpenApiConfig;
import com.vault.secure_vault.dto.User.*;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User related APIs")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })

   @PostMapping("/register")
    public ResponseEntity<@NotNull UserResponseDTO> register(
            @Valid @RequestBody UserRegistrationRequestDTO dto
   ){
       User user = userService.registerUser(dto);
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(mapUserToResponse(user));
   }

    @Operation(
            summary = "Get current user profile",
            description = "Returns logged-in user's profile details",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
   @GetMapping("/me")
    public ResponseEntity<@NotNull UserResponseDTO> getCurrentUser(
            Authentication authentication
   ){
       User user = userService.getCurrentUser(authentication.getName());
       return ResponseEntity.ok(mapUserToResponse(user));
   }

    @Operation(
            summary = "Update user profile",
            description = "Updates first name, last name, or photo URL",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
   @PutMapping("/me")
   public ResponseEntity<@NotNull UserResponseDTO> updateProfile(
           Authentication authentication,
           @RequestBody @Valid UserProfileUpdateDTO profileDTO
   ){
       User user = userService.updateProfile(authentication.getName(), profileDTO);
       return ResponseEntity.ok(mapUserToResponse(user));
   }

   @PostMapping("/me/spend-credits")
    public UserResponseDTO spendCredits(
            Authentication authentication,
            @Valid @RequestBody SpendCreditsRequestDTO request
   ){
       User user = userService.spendCreditsForStorage(authentication.getName(), request.credits());
       return mapUserToResponse(user);
   }

    @Operation(
            summary = "Upgrade storage using credits",
            description = "Consumes credits and increases storage limit",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @PostMapping("/me/storage/upgrade")
    public ResponseEntity<@NotNull StorageUpgradeResponseDTO> upgradeStorage(
            Authentication authentication,
            @RequestParam int credits
    ){
       User user = userService.upgradeStorage(authentication.getName(), credits);

       StorageUpgradeResponseDTO response =
               StorageUpgradeResponseDTO.builder()
                       .remainingCredits(user.getCredits())
                       .newStorageLimit(user.getStorageLimit())
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
