package com.motherskitchen.backend.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ACCOUNT_QUEUE = "account_email_queue";
    public static final String ORDER_QUEUE = "order_email_queue";

    public static final String EXCHANGE = "email_exchange";

    public static final String ACCOUNT_ROUTING_KEY = "email.account";
    public static final String ORDER_ROUTING_KEY = "email.order";

    @Bean
    public Queue accountQueue() {
        return new Queue(ACCOUNT_QUEUE, true);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding accountBinding() {
        return BindingBuilder
                .bind(accountQueue())
                .to(exchange())
                .with(ACCOUNT_ROUTING_KEY);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(exchange())
                .with(ORDER_ROUTING_KEY);
    }

    // ⭐ IMPORTANT: JSON converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ⭐ IMPORTANT: Attach converter to RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
