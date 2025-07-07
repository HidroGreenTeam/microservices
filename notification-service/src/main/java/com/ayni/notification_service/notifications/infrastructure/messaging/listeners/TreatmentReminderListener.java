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
    }

    @RabbitListener(queues = "${rabbitmq.queue.treatment-reminders}")
    public void handleReminder(TreatmentReminderMessage message) {
        try {
            logger.info("Received reminder message for step {} of treatment {}", 
                message.stepId(), message.treatmentId());

            String title = "🌿 Recordatorio para " + message.treatmentTitle();
            String body = "¡Hola! ✨\n\n" +
                          "Es momento de aplicar:\n" +
                          "🔹 *" + message.stepName() + "*\n\n" +
                          (message.stepDescription() != null && !message.stepDescription().trim().isEmpty() ? 
                              "📝 *Detalles:*\n" + message.stepDescription() + "\n\n" : "") +
                          "🌱 ¡Tus cultivos te lo agradecerán! 🌱";

            SendNotificationCommand command = new SendNotificationCommand(
                    message.profileId(),
                    NotificationType.TREATMENT,
                    NotificationChannel.WHATSAPP,
                    title,
                    body
            );

            Long notificationId = commandService.handle(command);

            logger.info("Treatment reminder processed successfully for step {} with notification {}", message.stepId(), notificationId);
        } catch (Exception e) {
            logger.error("Failed to process reminder message for step {}", message.stepId(), e);
            // No relanzamos la excepción para evitar que el mensaje vuelva a la cola
        }
    }
}