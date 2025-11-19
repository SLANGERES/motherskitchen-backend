package com.motherskitchen.backend.Models.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Email
    @Column(unique = true) // Email should be unique for login
    private String email;

    private String password;

    private String phoneNo;

    // 1. ADDED: This field is crucial for defining permissions/roles
    private String role;

    // --- Methods Required by UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converts the simple 'role' string (e.g., "DONOR") into a Spring Security Authority (e.g., "ROLE_DONOR")
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getUsername() {
        // Spring Security uses this method to get the unique identifier for login
        return this.email;
    }

    @Override
    public String getPassword() {
        // Returns the stored, HASHED password
        return this.password;
    }

    // --- Account Status Methods (Usually return true) ---

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}