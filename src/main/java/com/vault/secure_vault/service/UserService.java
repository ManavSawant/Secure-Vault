package com.vault.secure_vault.service;

import com.vault.secure_vault.dto.User.UserProfileUpdateDTO;
import com.vault.secure_vault.dto.User.UserRegistrationRequestDTO;
import com.vault.secure_vault.dto.User.UserResponseDTO;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int DEFAULT_CREDITS = 10;

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .photoUrl(user.getPhotoUrl())
                .credits(user.getCredits())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserResponseDTO registerUser(@Valid UserRegistrationRequestDTO dto) {
        if(userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .photoUrl(dto.getPhotoUrl())
                .password(passwordEncoder.encode(dto.getPassword()))
                .credits(DEFAULT_CREDITS)
                .totalStorageUsed(0)
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);
        return mapToResponseDTO(user);
    }

    public UserResponseDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return mapToResponseDTO(user);
    }

    public UserResponseDTO updateProfile(String email, UserProfileUpdateDTO dto){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if(dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if(dto.getLastName() != null) user.setLastName(dto.getLastName());
        if(dto.getPhotoUrl() != null) user.setPhotoUrl(dto.getPhotoUrl());

        User saveUser = userRepository.save(user);
        return mapToResponseDTO(saveUser);
    }


}
