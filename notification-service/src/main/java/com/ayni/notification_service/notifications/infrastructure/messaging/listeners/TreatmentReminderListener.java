package com.ayni.notification_service.notifications.infrastructure.messaging.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.infrastructure.messaging.dto.TreatmentReminderMessage;

@Component
public class TreatmentReminderListener {
    private static final Logger logger = LoggerFactory.getLogger(TreatmentReminderListener.class);

    private final NotificationCommandService commandService;

    public TreatmentReminderListener(NotificationCommandService commandService) {
        this.commandService = commandService;
        logger.info("TreatmentReminderListener initialized successfully");
    }

    @RabbitListener(queues = "${rabbitmq.queue.treatment-reminders}")
    public void handleReminder(TreatmentReminderMessage message) {
        try {
            logger.info("=== RECEIVED REMINDER MESSAGE FROM RABBITMQ ===");
            logger.info("Message details:");
            logger.info("- Step ID: {}", message.stepId());
            logger.info("- Treatment ID: {}", message.treatmentId());
            logger.info("- Profile ID: {}", message.profileId());
            logger.info("- Step Name: {}", message.stepName());
            logger.info("- Step Description: {}", message.stepDescription());
            logger.info("- Treatment Title: {}", message.treatmentTitle());
            logger.info("- Disease Type: {}", message.diseaseType());
            logger.info("- Scheduled Date: {}", message.scheduledDate());
            logger.info("- Reminder Minutes Before: {}", message.reminderMinutesBefore());

            String title = "🌿 Recordatorio para " + message.treatmentTitle();
            String body = "¡Hola! ✨\n\n" +
                          "Es momento de aplicar:\n" +
                          "🔹 *" + message.stepName() + "*\n\n" +
                          (message.stepDescription() != null && !message.stepDescription().trim().isEmpty() ? 
                              "📝 *Detalles:*\n" + message.stepDescription() + "\n\n" : "") +
                          "🌱 ¡Tus cultivos te lo agradecerán! 🌱";

            logger.info("=== CREATING NOTIFICATION COMMAND ===");
            logger.info("Title: {}", title);
            logger.info("Body: {}", body);
            logger.info("Profile ID: {}", message.profileId());
            logger.info("Notification Type: TREATMENT");
            logger.info("Notification Channel: WHATSAPP");

            SendNotificationCommand command = new SendNotificationCommand(
                    message.profileId(),
                    NotificationType.TREATMENT,
                    NotificationChannel.WHATSAPP,
                    title,
                    body
            );

            logger.info("=== SENDING NOTIFICATION COMMAND ===");
            Long notificationId = commandService.handle(command);

            logger.info("=== TREATMENT REMINDER PROCESSED SUCCESSFULLY ===");
            logger.info("Step ID: {}, Notification ID: {}", message.stepId(), notificationId);
            
        } catch (Exception e) {
            logger.error("=== ERROR PROCESSING REMINDER MESSAGE ===");
            logger.error("Step ID: {}, Error: {}", message.stepId(), e.getMessage(), e);
            // No relanzamos la excepción para evitar que el mensaje vuelva a la cola
        }
    }
}