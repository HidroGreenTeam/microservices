package com.ayni.notification_service.notifications.application.internal.eventhandlers;

import com.ayni.notification_service.notifications.domain.model.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Component
public class ActivityReminderEventHandler {

    @Autowired
    private NotificationCommandService notificationCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.activity-notifications}")
    public void handleActivityReminder(String message) {
        try {
            System.out.println("Received activity reminder: " + message);
            
            // Parse the JSON message
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            
            String userEmail = (String) eventData.get("userEmail");
            String activityName = (String) eventData.get("activityName");
            String treatmentName = (String) eventData.get("treatmentName");
            String scheduledDate = (String) eventData.get("scheduledDate");
            
            String subject = "Recordatorio de Actividad - " + activityName;
            String emailMessage = String.format(
                "Estimado usuario,\n\n" +
                "Le recordamos que tiene programada la siguiente actividad:\n\n" +
                "Actividad: %s\n" +
                "Tratamiento: %s\n" +
                "Fecha programada: %s\n\n" +
                "Saludos,\nEquipo HidroGreen",
                activityName, treatmentName, scheduledDate
            );
            
            // Send email notification
            notificationCommandService.sendNotification(userEmail, subject, emailMessage, NotificationType.EMAIL);
            
        } catch (Exception e) {
            System.err.println("Error processing activity reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Autowired
    private NotificationCommandService notificationCommandService2;

    @RabbitListener(queues = "${rabbitmq.queue.treatment-reminders}")
    public void handleTreatmentReminder(String message) {
        try {
            System.out.println("Received treatment reminder: " + message);
            
            // Parse the JSON message
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            
            String userEmail = (String) eventData.get("userEmail");
            String treatmentName = (String) eventData.get("treatmentName");
            String startDate = (String) eventData.get("startDate");
            
            String subject = "Recordatorio de Tratamiento - " + treatmentName;
            String emailMessage = String.format(
                "Estimado usuario,\n\n" +
                "Le recordamos sobre su tratamiento:\n\n" +
                "Tratamiento: %s\n" +
                "Fecha de inicio: %s\n\n" +
                "Saludos,\nEquipo HidroGreen",
                treatmentName, startDate
            );
            
            // Send email notification
            notificationCommandService2.sendNotification(userEmail, subject, emailMessage, NotificationType.EMAIL);
            
        } catch (Exception e) {
            System.err.println("Error processing treatment reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
