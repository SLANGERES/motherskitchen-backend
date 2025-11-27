package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.DTO.Email.AccountEmailCreationDTO;
import com.motherskitchen.backend.DTO.User.LoginDTO;
import com.motherskitchen.backend.DTO.User.LoginResponse;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.DTO.User.UserDTO;
import com.motherskitchen.backend.Security.Jwt.AuthUtil;
import com.motherskitchen.backend.Service.User.UserService;
import com.motherskitchen.backend.Service.Email.EmailService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class User {

    private final UserService userService;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // ---------------------- SIGNUP ----------------------
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody SignUpDTO request) {

        UserDTO user = userService.signup(request);

        // Send welcome mail
        AccountEmailCreationDTO emailData = AccountEmailCreationDTO.builder()
                .to(user.getEmail())
                .name(user.getName())
                .uid(user.getId().toString())
                .email(user.getEmail())
                .build();

        emailService.accountCreation(emailData);

        log.info("User created successfully UID={}", user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Signup successful");
        response.put("userId", user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------------- LOGIN ----------------------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginDTO request,
            HttpServletResponse response) {
        //DEBUG
        log.info("Login attempt for email: {}", request.getEmail());
        log.info("Login attempt for email: {}", request.getPassword());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Load full user from DB
            UserDTO user = userService.getUserByEmail(userDetails.getUsername());

            // Generate JWT
            String jwt = authUtil.generateAccessToken(user);

            // HttpOnly cookie
            Cookie cookie = new Cookie("token", jwt);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setDomain("motherskitchen.se");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setAttribute("SameSite", "None");
            response.addCookie(cookie);

            log.info("User logged in successfully UID={}", user.getId());

            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .token(jwt)
                            .role(user.getRole())
                            .build()
            );

        } catch (Exception e) {
            log.warn("Login failed for email={}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "INVALID"));
        }
    }

    // ---------------------- PROFILE ----------------------
    @GetMapping("/me")
    public ResponseEntity<UserDTO> profile(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDTO user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
}
