package com.mikkkkkkka.owner.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String OWNER_QUEUE = "owner_queue";
    public static final String OWNER_EXCHANGE = "owner_exchange";
    public static final String OWNER_ROUTING_KEY = "owner_routing_key";

    @Bean
    public Queue ownerQueue() {
        return new Queue(OWNER_QUEUE);
    }

    @Bean
    public TopicExchange ownerExchange() {
        return new TopicExchange(OWNER_EXCHANGE);
    }

    @Bean
    public Binding ownerBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(OWNER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}