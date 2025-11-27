package com.motherskitchen.backend.Security;

import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private String masterAdminRole;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // MASTER ADMIN
        if (email.equalsIgnoreCase(masterAdminEmail)) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(masterAdminEmail)
                    .password(masterAdminPassword)  // already encoded
                    .roles(masterAdminRole)
                    .build();
        }

        User dbUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(dbUser.getEmail())
                .password(dbUser.getPassword())  // already encoded in DB
                .roles(dbUser.getRole())
                .build();
    }
}
