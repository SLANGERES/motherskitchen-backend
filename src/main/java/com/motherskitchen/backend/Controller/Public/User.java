package com.motherskitchen.backend.Controller.Public;

import com.motherskitchen.backend.DTO.User.SignUpDTO;
import com.motherskitchen.backend.DTO.User.UserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class User {

    @PostMapping("/signup")
    public ResponseEntity<String>signUp(@Valid @RequestBody SignUpDTO request){

    }

    @PostMapping("/login")
    public ResponseEntity<String>signUp(@Valid @RequestBody LoginD request){

    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO>profile(//Accesstoken){

    }
}
