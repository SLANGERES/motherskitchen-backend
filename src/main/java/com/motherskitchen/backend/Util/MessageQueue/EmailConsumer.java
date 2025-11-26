package com.motherskitchen.backend.Util.MessageQueue;

import com.motherskitchen.backend.Configuration.RabbitMQConfig;
import com.motherskitchen.backend.DTO.Email.AccountEmailCreationDTO;
import com.motherskitchen.backend.DTO.Email.OrderEmail;
import com.motherskitchen.backend.Service.Email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    // ACCOUNT CREATION EMAIL
    @RabbitListener(queues = RabbitMQConfig.ACCOUNT_QUEUE)
    public void listen(AccountEmailCreationDTO data) {
        emailService.accountCreation(
                data.getTo(),
                data.getUid(),
                data.getName(),
                data.getEmail()
        );
    }

    // ORDER EMAIL
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void orderListen(OrderEmail data) {
        emailService.orderConform(
                data.getEmail(),
                data.getName(),
                data.getOrder()
        );
    }
}
