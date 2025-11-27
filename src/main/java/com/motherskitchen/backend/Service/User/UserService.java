package com.motherskitchen.backend.Service.User;

import com.motherskitchen.backend.DTO.User.LoginDTO;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.DTO.User.UserDTO;
import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.UserRepository;
import com.motherskitchen.backend.Security.UserDetailsServiceImpl;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${master.admin.email}")
    private String masterAdminEmail;

    @Transactional
    public UserDTO signup(SignUpDTO request) {

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User usr = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .phoneNo(request.getPhone())
                .role("USER")
                .build();
        User saveUser=userRepository.save(usr);

        return UserDTO.builder()
                .id(saveUser.getId())
                .name(saveUser.getName())
                .email(saveUser.getEmail())
                .password(saveUser.getPassword())
                .build();
    }
    public Optional<UserDTO> login(LoginDTO request){
        Optional<User> usr = userRepository.findByEmail(request.getEmail());

        if (usr.isEmpty()) return Optional.empty();

        User user = usr.get();
        UserDTO newUsr=UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .build();

        log.info("Password matches? {}", passwordEncoder.matches(request.getPassword(), user.getPassword()));

        // Compare raw password with hashed password
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            return Optional.empty();
        }
        return Optional.of(newUsr);
    }

    public UserDTO getUserByEmail(String email) {
        // Check if it's the master admin
        if (email.equalsIgnoreCase(masterAdminEmail)) {
            return UserDTO.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000000")) // Special UUID for master admin
                    .name("Master Admin")
                    .email(masterAdminEmail)
                    .phoneNo(null)
                    .role("ADMIN")
                    .build();
        }

        // Otherwise, load from database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .role("USER")
                .build();
    }
    public UserDTO getUserById(UUID id) {
        // Check if it's the master admin
        // Otherwise, load from database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .build();
    }




}
