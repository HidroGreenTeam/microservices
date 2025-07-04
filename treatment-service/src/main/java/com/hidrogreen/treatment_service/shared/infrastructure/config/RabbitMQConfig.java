package com.hidrogreen.treatment_service.shared.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.activity-notifications}")
    private String activityNotificationsQueue;
    
    @Value("${rabbitmq.queue.treatment-reminders}")
    private String treatmentRemindersQueue;
    
    @Value("${rabbitmq.queue.diagnosis-queue}")
    private String diagnosisQueue;
    
    @Value("${rabbitmq.exchange.notifications}")
    private String notificationsExchange;

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(notificationsExchange);
    }

    @Bean
    public Queue activityNotificationsQueue() {
        return QueueBuilder.durable(activityNotificationsQueue).build();
    }

    @Bean
    public Queue treatmentRemindersQueue() {
        return QueueBuilder.durable(treatmentRemindersQueue).build();
    }

    @Bean
    public Queue diagnosisQueue() {
        return QueueBuilder.durable(diagnosisQueue).build();
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