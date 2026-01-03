package com.vault.secure_vault.controller;

import com.vault.secure_vault.dto.User.UserProfileUpdateDTO;
import com.vault.secure_vault.dto.User.UserRegistrationRequestDTO;
import com.vault.secure_vault.dto.User.UserResponseDTO;
import com.vault.secure_vault.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

   @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO dto){
       return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(dto));
   }

   @GetMapping("/me")
    public UserResponseDTO getCurrentUser(Authentication authentication){
       String email = authentication.getName();
       return userService.getCurrentUser(email);
   }

   @PutMapping("/me")
   public UserResponseDTO updateProfile(Authentication authentication, @RequestBody @Valid UserProfileUpdateDTO profileDTO){
       String email = authentication.getName();
       return userService.updateProfile(email,profileDTO);
   }

}
