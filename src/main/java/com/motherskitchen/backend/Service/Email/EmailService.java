package com.motherskitchen.backend.Service.Email;

import com.motherskitchen.backend.DTO.Email.AccountEmailCreationDTO;
import com.motherskitchen.backend.DTO.Email.OrderEmail;
import com.motherskitchen.backend.DTO.Party.PartyOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${owner.email}")
    private String ownerEmail;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private void sendHtmlEmail(String to, String subject, String htmlContent) {

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "Mother's Kitchen", "email", "support@motherskitchen.se"));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", subject);
        body.put("htmlContent", htmlContent);

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        restTemplate.postForObject(BREVO_URL, entity, String.class);
    }

    // --------------------------
    // ACCOUNT CREATION EMAIL
    // --------------------------
    @Async
    public void accountCreation(AccountEmailCreationDTO request) {
        try {
            String html = EmailHTML.accountCreation(
                    request.getName(),
                    request.getEmail(),
                    request.getUid(),
                    "https://www.motherskitchen.se/"
            );

            sendHtmlEmail(request.getTo(), "Welcome to Mother's Kitchen!", html);
            System.out.println("-------> Account creation email sent to: " + request.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------
    // ORDER CONFIRMATION EMAIL
    // --------------------------
    @Async
    public void orderConform(OrderEmail request) {
        try {
            String html = EmailHTML.orderConfirmation(
                    request.getOrder(),
                    request.getName()
            );

            sendHtmlEmail(request.getEmail(), "Order Confirmation – Mother's Kitchen", html);
            System.out.println("-------> Order confirmation email sent to: " + request.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------
    // PARTY ORDER EMAIL (to OWNER)
    // --------------------------
    @Async
    public void partyOrderEmail(PartyOrderRequest request) {
        try {
            String html = EmailHTML.partyOrderEmail(
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getDate(),
                    request.getGuests(),
                    request.getVegNonVeg(),
                    request.getCombo(),
                    request.getMessage()
            );

            sendHtmlEmail(ownerEmail, "New Party Order – Mother's Kitchen", html);
            System.out.println("-------> Party order email sent to owner: " + ownerEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
