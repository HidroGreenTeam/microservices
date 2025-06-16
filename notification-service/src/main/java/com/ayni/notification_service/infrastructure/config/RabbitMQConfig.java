package com.ayni.notification_service.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@Configuration
public class RabbitMQConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    
    @Value("${rabbitmq.exchange.notifications}")
    private String notificationsExchange;
    
    @Value("${rabbitmq.queue.activity-notifications}")
    private String activityNotificationsQueue;
    
    @Value("${rabbitmq.queue.treatment-reminders}")
    private String treatmentRemindersQueue;
    
    @Value("${rabbitmq.routing.key.activity}")
    private String activityRoutingKey;
    
    @Value("${rabbitmq.routing.key.treatment}")
    private String treatmentRoutingKey;
    
    @PostConstruct
    public void init() {
        logger.info("RabbitMQ Configuration loaded in Notification Service:");
        logger.info("Exchange: {}", notificationsExchange);
        logger.info("Activity Queue: {}", activityNotificationsQueue);
        logger.info("Treatment Queue: {}", treatmentRemindersQueue);
        logger.info("Activity Routing Key: {}", activityRoutingKey);
        logger.info("Treatment Routing Key: {}", treatmentRoutingKey);
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
    public Queue activityNotificationsQueue() {
        logger.info("Creating Queue: {}", activityNotificationsQueue);
        return QueueBuilder.durable(activityNotificationsQueue).build();
    }
    
    @Bean
    public Queue treatmentRemindersQueue() {
        logger.info("Creating Queue: {}", treatmentRemindersQueue);
        return QueueBuilder.durable(treatmentRemindersQueue).build();
    }
    
    @Bean
    public Binding activityNotificationsBinding() {
        logger.info("Creating Binding for activity notifications: queue={}, exchange={}, routingKey={}", 
                   activityNotificationsQueue, notificationsExchange, activityRoutingKey);
        return BindingBuilder
                .bind(activityNotificationsQueue())
                .to(notificationsExchange())
                .with(activityRoutingKey);
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
}
