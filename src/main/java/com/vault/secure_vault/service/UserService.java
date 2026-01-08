package com.vault.secure_vault.service;
import com.vault.secure_vault.dto.User.UserProfileUpdateDTO;
import com.vault.secure_vault.dto.User.UserRegistrationRequestDTO;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.util.constant.StorageConstant;
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

    public User registerUser(UserRegistrationRequestDTO request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .photoUrl(request.photoUrl())
                .password(passwordEncoder.encode(request.password()))
                .credits(DEFAULT_CREDITS)
                .storageLimit(DEFAULT_STORAGE_LIMIT)
                .storageUsed(0L)
                .createdAt(Instant.now())
                .isDeleted(false)
                .build();
         return userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found "+email));
    }

    public User updateProfile(String email, UserProfileUpdateDTO request){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if(request.firstName() != null) user.setFirstName(request.firstName());
        if(request.lastName() != null) user.setLastName(request.lastName());
        if(request.photoUrl() != null) user.setPhotoUrl(request.photoUrl());

        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return getCurrentUser(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User spendCreditsForStorage(String email, int creditsToSpend){

        if(creditsToSpend <= 0) throw new IllegalArgumentException("creditsToSpend must be greater than 0");

        User user = getCurrentUser(email);

        if(user.getCredits() < creditsToSpend) throw new RuntimeException("Insufficient credit");

        long extraStorage = creditsToSpend * StorageConstant.STORAGE_PER_CREDIT;

        user.setCredits(user.getCredits() - creditsToSpend);
        user.setStorageLimit(user.getStorageLimit() + extraStorage);

        return userRepository.save(user);
    }

    @Transactional
    public User upgradeStorage(String email, int credits) {

       if(credits <= 0) throw new IllegalArgumentException("credits must be greater than 0");

       User user = getCurrentUser(email);

       if(user.getCredits() < credits) throw new IllegalStateException("Insufficient credit");

       long bytesToAdd = credits * StorageConstant.STORAGE_PER_CREDIT;

       user.setCredits(user.getCredits() - credits);
       user.setStorageLimit(user.getStorageLimit() + bytesToAdd);

       return userRepository.save(user);
    }

}
