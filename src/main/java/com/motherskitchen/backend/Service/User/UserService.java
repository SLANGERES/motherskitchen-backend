package com.motherskitchen.backend.Service.User;

import com.motherskitchen.backend.DTO.User.LoginDTO;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.Models.User.User;
import com.motherskitchen.backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public boolean signup(SignUpDTO request) {

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User usr = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .phoneNo(request.getPhoneNo())
                .build();

        userRepository.save(usr);
        return true;
    }
    public boolean login(LoginDTO request){
        Optional<User> usr = userRepository.findByEmail(request.getEmail());

        if (usr.isEmpty()) return false;

        User user = usr.get();

        // Compare raw password with hashed password
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }

    public Optional<User> userprofile(String userID){
        return userRepository.findById(UUID.fromString(userID));
    }

}
