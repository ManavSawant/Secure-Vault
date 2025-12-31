package com.vault.secure_vault.controller;

import com.vault.secure_vault.Auth.AuthService;
import com.vault.secure_vault.Auth.UserLoginRequestDTO;
import com.vault.secure_vault.Auth.UserLoginResponseDTO;
import lombok.RequiredArgsConstructor;
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
    public UserLoginResponseDTO login(@RequestBody UserLoginRequestDTO LoginRequestDTO){
        return authService.login(LoginRequestDTO);
    }
}
