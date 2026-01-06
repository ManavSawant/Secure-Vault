package com.vault.secure_vault.service;

import com.vault.secure_vault.dto.User.UserProfileUpdateDTO;
import com.vault.secure_vault.dto.User.UserRegistrationRequestDTO;
import com.vault.secure_vault.dto.User.UserResponseDTO;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.util.constant.StorageConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int DEFAULT_CREDITS = 10;
    private static final long DEFAULT_STORAGE_LIMIT = 500*1024*1024;

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .photoUrl(user.getPhotoUrl())
                .credits(user.getCredits())
                .storageUsed(user.getStorageUsed())
                .storageLimit(user.getStorageLimit())
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
                .storageLimit(DEFAULT_STORAGE_LIMIT)
                .createdAt(Instant.now())
                .isDeleted(false)
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

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public UserResponseDTO spendCreditsForStorage(String email, int creditsToSpend){

        if(creditsToSpend <= 0) throw new IllegalArgumentException("creditsToSpend must be greater than 0");

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getCredits() < creditsToSpend) throw new RuntimeException("Insufficient credit");

        long extraStorage = creditsToSpend * StorageConstant.STORAGE_PER_CREDIT;

        user.setCredits(user.getCredits() - creditsToSpend);
        user.setStorageLimit(user.getStorageLimit() + extraStorage);

        userRepository.save(user);

        return mapToResponseDTO(user);
    }

}
