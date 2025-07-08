package com.ayni.notification_service.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Configuration
public class RabbitMQConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    
    @Value("${rabbitmq.exchange.notifications}")
    private String notificationsExchange;
    
    @Value("${rabbitmq.queue.treatment-reminders}")
    private String treatmentRemindersQueue;
    
    @Value("${rabbitmq.routing.key.treatment}")
    private String treatmentRoutingKey;
    
    // Subscription-related configuration
    public static final String SUBSCRIPTION_NOTIFICATION_QUEUE = "subscription.notification.queue";
    public static final String SUBSCRIPTION_EXCHANGE = "subscription.exchange";
    
    @PostConstruct
    public void init() {
        logger.info("RabbitMQ Configuration loaded in Notification Service:");
        logger.info("Exchange: {}", notificationsExchange);
        logger.info("Treatment Reminders Queue: {}", treatmentRemindersQueue);
        logger.info("Treatment Routing Key: {}", treatmentRoutingKey);
    }

    /**
     * Custom message converter that ignores __TypeId__ header to handle
     * DTO class name mismatches between microservices
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        logger.info("Creating custom Jackson2JsonMessageConverter that ignores TypeId headers");
        // This tells the converter to use the method signature type instead of __TypeId__ header
        // This fixes the issue where treatment-service and notification-service have different package names
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter; 
    }

    /**
     * Custom listener container factory with our message converter
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        logger.info("Creating custom RabbitListenerContainerFactory with TypeId-ignoring message converter");
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        logger.info("Creating RabbitAdmin bean in Notification Service");
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }
    
    @Bean
    public TopicExchange notificationsExchange() {
        logger.info("Creating TopicExchange: {}", notificationsExchange);
        return new TopicExchange(notificationsExchange);
    }
    
    @Bean
    public Queue treatmentRemindersQueue() {
        logger.info("Creating Queue: {}", treatmentRemindersQueue);
        return QueueBuilder.durable(treatmentRemindersQueue).build();
    }
    
    @Bean
    public Binding treatmentRemindersBinding() {
        logger.info("Creating Binding for treatment reminders: queue={}, exchange={}, routingKey={}", 
                   treatmentRemindersQueue, notificationsExchange, treatmentRoutingKey);
        return BindingBuilder
                .bind(treatmentRemindersQueue())
                .to(notificationsExchange())
                .with(treatmentRoutingKey);
    }

    // Subscription-related beans
    @Bean
    public TopicExchange subscriptionExchange() {
        logger.info("Creating Subscription TopicExchange: {}", SUBSCRIPTION_EXCHANGE);
        return new TopicExchange(SUBSCRIPTION_EXCHANGE);
    }

    @Bean
    public Queue subscriptionNotificationQueue() {
        logger.info("Creating Subscription Queue: {}", SUBSCRIPTION_NOTIFICATION_QUEUE);
        return QueueBuilder.durable(SUBSCRIPTION_NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Binding subscriptionNotificationBinding() {
        logger.info("Creating Binding for subscription notifications: queue={}, exchange={}", 
                   SUBSCRIPTION_NOTIFICATION_QUEUE, SUBSCRIPTION_EXCHANGE);
        return BindingBuilder
                .bind(subscriptionNotificationQueue())
                .to(subscriptionExchange())
                .with("subscription.*");
    }
}
