package com.vault.secure_vault.security;

import com.vault.secure_vault.model.User;
import com.vault.secure_vault.repository.UserRepository;
import com.vault.secure_vault.service.UserService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public UserDetails loadUserByUsername(String eamil) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(eamil)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with eamil " + eamil));

        return buildUserDetails(user);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
