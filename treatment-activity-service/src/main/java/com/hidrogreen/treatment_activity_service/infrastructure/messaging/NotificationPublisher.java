package com.hidrogreen.treatment_activity_service.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hidrogreen.treatment_activity_service.infrastructure.config.RabbitMQConfig;

@Service
public class NotificationPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    
    @Autowired
    public NotificationPublisher(RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
    }
    
    public void publishActivityReminder(ActivityReminderEvent event) {
        rabbitTemplate.convertAndSend(
            rabbitMQConfig.getNotificationsExchange(),
            rabbitMQConfig.getActivityRoutingKey(),
            event
        );
        System.out.println("Activity reminder published: " + event.getActivityName());
    }
    
    public void publishTreatmentReminder(Object event) {
        rabbitTemplate.convertAndSend(
            rabbitMQConfig.getNotificationsExchange(),
            rabbitMQConfig.getTreatmentRoutingKey(),
            event
        );
        System.out.println("Treatment reminder published");
    }
}
