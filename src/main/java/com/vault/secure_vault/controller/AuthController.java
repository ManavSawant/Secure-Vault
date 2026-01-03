package com.vault.secure_vault.controller;

import com.vault.secure_vault.Auth.AuthResponseDTO;
import com.vault.secure_vault.Auth.AuthService;
import com.vault.secure_vault.Auth.UserLoginRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO LoginRequestDTO){
        return ResponseEntity.ok(authService.login(LoginRequestDTO));
    }
}
