package com.motherskitchen.backend.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void orderConformation(String to, String orderId) {
        SimpleMailMessage message = new SimpleMailMessage();

        String subject = "Order Confirmation - ORDER ID #" + orderId;
        String text = "Dear Valued Customer,\n\n"
                + "Your order has been successfully placed!\n"
                + "Thank you for choosing Mother's Kitchen.\n\n"
                + "Order ID: " + orderId + "\n\n"
                + "We will notify you once your order is ready.\n\n"
                + "Best regards,\n"
                + "Mother's Kitchen Team";

        message.setFrom("team@motherskitchen.se");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

}
