package com.vault.secure_vault.security;

import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;


    /**
     * Loads user details by email for Spring Security authentication.
     * This method is used internally by Spring during authentication.
     *
     * @param email the user's email (used as username)
     * @return UserDetails object used by Spring Security
     * @throws UsernameNotFoundException if user does not exist or is deleted
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found or deleted with email: " + email)
                );
        return buildUserDetails(user);
    }

    /**
     * Returns authorities (roles) for the given user.
     * Currently, every user has ROLE_USER.
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    /**
     * Builds Spring Security UserDetails from domain User entity.
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
