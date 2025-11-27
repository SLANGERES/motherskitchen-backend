package com.motherskitchen.backend.Security;

import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${master.admin.email}")
    private String masterAdminEmail;

    @Value("${master.admin.password}")
    private String masterAdminPassword;

    @Value("${master.admin.role}")
    private String masterAdminRole;   // ADMIN

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // MASTER ADMIN HANDLING
        if (email.equalsIgnoreCase(masterAdminEmail)) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(masterAdminEmail)
                    .password(masterAdminPassword)   // MUST BE ENCODED
                    .roles(masterAdminRole)          // e.g., ADMIN
                    .build();
        }

        // NORMAL USER
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())       // MUST BE ENCODED IN DB
                .roles(user.getRole())   // ensure it becomes "ADMIN"
                .build();
    }
}
