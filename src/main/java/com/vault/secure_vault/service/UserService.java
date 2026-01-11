package com.vault.secure_vault.service;
import com.vault.secure_vault.dto.User.StorageUpgradeRequestDTO;
import com.vault.secure_vault.dto.User.UserProfileUpdateDTO;
import com.vault.secure_vault.dto.User.UserRegistrationRequestDTO;
import com.vault.secure_vault.exceptions.User.InsufficientCreditsException;
import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.util.constant.StorageConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


/**
 * Service layer responsible for all user-related business logic.
 * <p>
 * This includes:
 * <ul>
 *     <li>User registration</li>
 *     <li>Profile updates</li>
 *     <li>Storage upgrades</li>
 *     <li>Credit management</li>
 * </ul>
 *
 * <p><b>Important:</b> Controllers must NOT contain business logic.
 * This class is the single source of truth for user domain rules.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int DEFAULT_CREDITS = 10;
    private static final long DEFAULT_STORAGE_LIMIT = 500*1024*1024;

    /**
     * Registers a new user in the system.
     *
     * @param request user registration data
     * @return persisted User entity
     * @throws IllegalStateException if email already exists
     */
    public User registerUser(UserRegistrationRequestDTO request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .credits(DEFAULT_CREDITS)
                .storageLimit(DEFAULT_STORAGE_LIMIT)
                .storageUsed(0L)
                .createdAt(Instant.now())
                .isDeleted(false)
                .build();
         return userRepository.save(user);
    }


    /**
     * Fetches the currently authenticated user by email.
     *
     * @param email user email
     * @return User entity
     * @throws UsernameNotFoundException if user does not exist
     */
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found "+email));
    }



    /**
     * Updates user profile fields.
     * Only non-null fields from request are updated.
     *
     * @param email   user email
     * @param request profile update request
     * @return updated User entity
     */
    public User updateProfile(String email, UserProfileUpdateDTO request){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if(request.firstName() != null) user.setFirstName(request.firstName());
        if(request.lastName() != null) user.setLastName(request.lastName());
        if(request.photoUrl() != null) user.setPhotoUrl(request.photoUrl());

        return userRepository.save(user);
    }

    /**
     * Returns user by email.
     * This is a helper wrapper over getCurrentUser.
     *
     * @param email user email
     * @return User entity
     */
    public User getByEmail(String email) {
        return getCurrentUser(email);
    }

    /**
     * Persists user entity.
     *
     * @param user user entity
     */
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * Spends credits to increase storage capacity.
     *
     * @param email           user email
     * @param creditsToSpend number of credits to spend
     * @return updated User entity
     * @throws IllegalArgumentException       if creditsToSpend <= 0
     * @throws InsufficientCreditsException  if user does not have enough credits
     */
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

    /**
     * Upgrades user storage based on request payload.
     *
     * @param email   user email
     * @param request storage upgrade request
     * @return updated User entity
     * @throws InsufficientCreditsException if credits are insufficient
     */
    @Transactional
    public User upgradeStorage(String email, StorageUpgradeRequestDTO request) {

        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        int creditsToSpend = request.credits();

        if (user.getCredits() < creditsToSpend) {
            throw new InsufficientCreditsException("Not enough credits");
        }

        long additionalStorage = creditsToSpend * DEFAULT_STORAGE_LIMIT;

        user.setCredits(user.getCredits() - creditsToSpend);
        user.setStorageLimit(user.getStorageLimit() + additionalStorage);

        return userRepository.save(user);
    }

}
