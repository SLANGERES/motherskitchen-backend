package com.motherskitchen.backend.Service.User;

import com.motherskitchen.backend.DTO.User.LoginDTO;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.DTO.User.UserDTO;
import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.UserRepository;
import com.motherskitchen.backend.Security.UserDetailsServiceImpl;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public UUID signup(SignUpDTO request) {

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User usr = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .phoneNo(request.getPhoneNo())
                .role("USER")
                .build();

        return userRepository.save(usr).getId();
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

        // Compare raw password with hashed password
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            return Optional.empty();
        }
        return Optional.of(newUsr);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .build();
    }




}
