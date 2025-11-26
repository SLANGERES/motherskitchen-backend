package com.motherskitchen.backend.Service.Email;

import com.motherskitchen.backend.DTO.Email.AccountEmailCreationDTO;
import com.motherskitchen.backend.DTO.Email.OrderEmail;
import com.motherskitchen.backend.DTO.Party.PartyOrderRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void accountCreation(AccountEmailCreationDTO request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = "Welcome to Mothers Kitchen!";

            String html = EmailHTML.accountCreation(
                    request.getName(),
                    request.getEmail(),
                    request.getUid(),
                    "https://www.motherskitchen.se/"
            );

            helper.setFrom("support@motherskitchen.se");
            helper.setTo(request.getTo());
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML email

            mailSender.send(message);
            System.out.println("---------> send account creation mail: " + request.getEmail());

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    @Async
    public void orderConform(OrderEmail request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = "Order Confirmation – Mother's Kitchen";

            String html = EmailHTML.orderConfirmation(
                    request.getOrder(),
                    request.getName()
            );

            helper.setFrom("support@motherskitchen.se");
            helper.setTo(request.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML email

            mailSender.send(message);
            System.out.println("---------> send order conform mail to: " + request.getEmail());

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    @Async
    public void partyOrderEmail(PartyOrderRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = "Hi there is Party Order – Mother's Kitchen";

            String html = EmailHTML.partyOrderEmail(
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getDate(),
                    request.getGuests(),
                    request.getCombo(),
                    request.getMessage()
            );

            helper.setFrom("support@motherskitchen.se");
            helper.setTo("${owner.email}");
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML email

            mailSender.send(message);
            System.out.println("---------> send order conform mail to: " + "${owner.email}");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
