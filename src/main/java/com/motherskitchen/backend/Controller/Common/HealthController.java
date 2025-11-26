package com.motherskitchen.backend.Controller.Common;

import com.motherskitchen.backend.DTO.Party.PartyOrderRequest;
import com.motherskitchen.backend.Service.Email.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    @PostMapping()
    public ResponseEntity<String> health(){
        return new ResponseEntity<>("Health OK", HttpStatus.OK);
    }
}
