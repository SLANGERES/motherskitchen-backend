package com.motherskitchen.backend.Service.Email;

import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public void accountCreation(String to, String uid, String name, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = "Welcome to Mothers Kitchen!";

            String html = EmailHTML.accountCreation(
                    name,
                    email,
                    uid,
                    "https://www.motherskitchen.se/"
            );

            helper.setFrom("support@motherskitchen.se");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML email

            mailSender.send(message);
            System.out.println("---------> send account creation mail: " + to);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void orderConform(String to, String name, OrdersDTO orderDetail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = "Order Confirmation – Mother's Kitchen";

            String html = EmailHTML.orderConfirmation(
                    orderDetail,
                    name
            );

            helper.setFrom("support@motherskitchen.se");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML email

            mailSender.send(message);
            System.out.println("---------> send order conform mail to: " + to);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
