package com.motherskitchen.backend.Util.MessageQueue;


import com.motherskitchen.backend.Configuration.RabbitMQConfig;
import com.motherskitchen.backend.DTO.Email.AccountEmailCreationDTO;
import com.motherskitchen.backend.DTO.Email.OrderEmail;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAccountMail(AccountEmailCreationDTO emailData) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ACCOUNT_ROUTING_KEY,
                emailData
        );
    }

    public void sendOrderMail(OrderEmail emailData) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                emailData
        );
    }
}
