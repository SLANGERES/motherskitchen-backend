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

    @Value("${master.admin.email}")
    private String masterAdminEmail;

    @Value("${master.admin.password}")
    private String masterAdminPassword;

    @Value("${master.admin.role}")
    private String masterAdminRole;  // e.g., ADMIN

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1️⃣ Check master admin

        if (email.equalsIgnoreCase(masterAdminEmail)) {

            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(masterAdminEmail)
                    .password(masterAdminPassword)
                    .roles(masterAdminRole)  // ROLE_ADMIN
                    .build();
        }

        // 2️⃣ Load DB user
        com.motherskitchen.backend.Models.User.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
