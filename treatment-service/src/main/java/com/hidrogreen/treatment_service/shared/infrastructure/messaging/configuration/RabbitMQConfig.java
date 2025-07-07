package com.hidrogreen.treatment_service.shared.infrastructure.messaging.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for Treatment Service
 */
@Configuration
public class RabbitMQConfig {

    public static final String DIAGNOSIS_QUEUE = "diagnosis_queue";
    public static final String TREATMENT_NOTIFICATIONS_QUEUE = "treatment.notifications";
    public static final String ACTIVITY_REMINDERS_QUEUE = "activity.reminders";

    @Bean
    public Queue diagnosisQueue() {
        return QueueBuilder.durable(DIAGNOSIS_QUEUE).build();
    }

    @Bean
    public Queue treatmentNotificationsQueue() {
        return QueueBuilder.durable(TREATMENT_NOTIFICATIONS_QUEUE).build();
    }

    @Bean
    public Queue activityRemindersQueue() {
        return QueueBuilder.durable(ACTIVITY_REMINDERS_QUEUE).build();
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
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.AUTO);
        return factory;
    }
}
