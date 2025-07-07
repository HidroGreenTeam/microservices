package com.hidrogreen.treatment_service.shared.infrastructure.messaging.services;

import com.hidrogreen.treatment_service.shared.infrastructure.messaging.dto.TreatmentReminderMessage;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {
    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String notificationsExchange;
    private final String treatmentRoutingKey;

    public ReminderService(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.notifications}") String notificationsExchange,
            @Value("${rabbitmq.routing.key.treatment}") String treatmentRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationsExchange = notificationsExchange;
        this.treatmentRoutingKey = treatmentRoutingKey;
    }

    public void sendReminder(TreatmentStep step) {
        try {
            TreatmentReminderMessage message = new TreatmentReminderMessage(
                step.getId(),
                step.getTreatment().getId(),
                step.getTreatment().getProfileId(),
                step.getName(),
                step.getDescription(),
                step.getTreatment().getTitle(),
                step.getTreatment().getDiseaseType(),
                step.getScheduledDate(),
                step.getReminderMinutesBefore()
            );

            logger.info("Sending reminder for step {} of treatment {}", 
                step.getId(), step.getTreatment().getId());

            rabbitTemplate.convertAndSend(
                notificationsExchange,
                treatmentRoutingKey,
                message
            );

            logger.info("Reminder sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send reminder for step {}", step.getId(), e);
            throw new RuntimeException("Failed to send reminder", e);
        }
    }
} 