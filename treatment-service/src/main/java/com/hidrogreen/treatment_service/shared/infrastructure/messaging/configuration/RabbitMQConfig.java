package com.hidrogreen.treatment_service.shared.infrastructure.messaging.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

/**
 * RabbitMQ Configuration for Treatment Service
 */
@Configuration
public class RabbitMQConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${rabbitmq.exchange.notifications}")
    private String notificationsExchange;

    @Value("${rabbitmq.queue.diagnosis}")
    private String diagnosisQueue;

    @Value("${rabbitmq.queue.treatment-reminders}")
    private String treatmentRemindersQueue;

    @Value("${rabbitmq.routing.key.treatment}")
    private String treatmentRoutingKey;

    @PostConstruct
    public void init() {
        logger.info("RabbitMQ Configuration loaded in Treatment Service:");
        logger.info("Exchange: {}", notificationsExchange);
        logger.info("Diagnosis Queue: {}", diagnosisQueue);
        logger.info("Treatment Reminders Queue: {}", treatmentRemindersQueue);
        logger.info("Treatment Routing Key: {}", treatmentRoutingKey);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        logger.info("Creating RabbitAdmin bean in Treatment Service");
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
    public Queue diagnosisQueue() {
        logger.info("Creating Queue: {}", diagnosisQueue);
        return QueueBuilder.durable(diagnosisQueue).build();
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

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        factory.setDefaultRequeueRejected(false);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
}
