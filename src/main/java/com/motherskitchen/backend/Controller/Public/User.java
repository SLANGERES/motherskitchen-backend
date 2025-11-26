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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class User {
    private final UserService userService;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<UUID>signUp(@Valid @RequestBody SignUpDTO request){
        UserDTO user=userService.signup(request);

        AccountEmailCreationDTO EmailData= AccountEmailCreationDTO.builder()
                .to(user.getEmail())
                .name(user.getName())
                .uid(user.getId().toString())
                .email(user.getEmail())
                .build();
        emailService.accountCreation(EmailData);
        return new ResponseEntity<>(user.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginDTO request,
            HttpServletResponse response) {

        try {
            // 1️⃣ Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 2️⃣ Load full user info
            UserDTO user = userService.getUserByEmail(userDetails.getUsername());

            // 3️⃣ Generate JWT
            String jwt = authUtil.generateAccessToken(user);

            // 4️⃣ Set HttpOnly Cookie
            Cookie cookie = new Cookie("token", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);

            // 5️⃣ Build Response
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(jwt)
                    .role(user.getRole()).build();

            // 6️⃣ Return JSON
            return new ResponseEntity<>(loginResponse,HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "INVALID"));
        }
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
