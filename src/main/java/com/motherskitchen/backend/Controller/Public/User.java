package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.DTO.User.LoginDTO;
import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.DTO.User.UserDTO;
import com.motherskitchen.backend.Security.Jwt.AuthUtil;
import com.motherskitchen.backend.Service.User.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class User {
    private final UserService userService;
    private final AuthUtil authUtil;

    @PostMapping("/signup")
    public ResponseEntity<UUID>signUp(@Valid @RequestBody SignUpDTO request){
        return new ResponseEntity<>(userService.signup(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginDTO request,
            @CookieValue(value = "token", required = false) String token,
            HttpServletResponse response) {

        Optional<UserDTO> usr = userService.login(request);
        if (usr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDTO user = usr.get();
        String jwtToken = authUtil.generateAccessTokenDonor(user);

        // Create secure cookie
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);       // Set to false for localhost HTTP (change to true in production with HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful");
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> profile(@AuthenticationPrincipal UserDetails userDetails) {
        // If no authenticated user, Spring Security will return 401 automatically
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Fetch the full user from the database using the email
        String email = userDetails.getUsername();
        UserDTO user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

}
