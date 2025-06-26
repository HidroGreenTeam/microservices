package com.hidrogreen.subscription_service.shared.infrastructure.messaging.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String SUBSCRIPTION_NOTIFICATION_QUEUE = "subscription.notification.queue";

    // Exchange names
    public static final String SUBSCRIPTION_EXCHANGE = "subscription.exchange";

    // Routing keys
    public static final String SUBSCRIPTION_CREATED_ROUTING_KEY = "subscription.created";
    public static final String SUBSCRIPTION_CANCELLED_ROUTING_KEY = "subscription.cancelled";
    public static final String SUBSCRIPTION_RENEWED_ROUTING_KEY = "subscription.renewed";

    @Bean
    public TopicExchange subscriptionExchange() {
        return new TopicExchange(SUBSCRIPTION_EXCHANGE);
    }

    @Bean
    public Queue subscriptionNotificationQueue() {
        return QueueBuilder.durable(SUBSCRIPTION_NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding subscriptionCreatedBinding() {
        return BindingBuilder
                .bind(subscriptionNotificationQueue())
                .to(subscriptionExchange())
                .with(SUBSCRIPTION_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding subscriptionCancelledBinding() {
        return BindingBuilder
                .bind(subscriptionNotificationQueue())
                .to(subscriptionExchange())
                .with(SUBSCRIPTION_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public Binding subscriptionRenewedBinding() {
        return BindingBuilder
                .bind(subscriptionNotificationQueue())
                .to(subscriptionExchange())
                .with(SUBSCRIPTION_RENEWED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
