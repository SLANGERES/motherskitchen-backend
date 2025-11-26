package com.motherskitchen.backend.Controller.Common;

import com.motherskitchen.backend.DTO.Party.PartyOrderRequest;
import com.motherskitchen.backend.Service.Email.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/party-order")
@RequiredArgsConstructor
public class PartyController {
    private final EmailService emailService;
    @PostMapping()
    public ResponseEntity<?> partyOrderRequest(@Valid @RequestBody PartyOrderRequest request){
        emailService.partyOrderEmail(request);
        return ResponseEntity.ok("Successfully");

    }
}
