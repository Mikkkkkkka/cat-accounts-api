package com.mikkkkkkka.cat.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CAT_QUEUE = "cat_queue";
    public static final String CAT_EXCHANGE = "cat_exchange";
    public static final String CAT_ROUTING_KEY = "cat_routing_key";

    @Bean
    public Queue catQueue() {
        return new Queue(CAT_QUEUE);
    }

    @Bean
    public TopicExchange catExchange() {
        return new TopicExchange(CAT_EXCHANGE);
    }

    @Bean
    public Binding catBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CAT_ROUTING_KEY);
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