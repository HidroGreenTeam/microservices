package com.ayni.notification_service.notifications.application.internal.eventhandlers;

import com.ayni.notification_service.notifications.domain.model.events.ActivityDueEvent;
import com.ayni.notification_service.notifications.domain.model.commands.SendActivityReminderCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Service
public class ActivityDueEventHandler {
    
    private static final Logger log = LoggerFactory.getLogger(ActivityDueEventHandler.class);
    
    private final NotificationCommandService notificationCommandService;
    
    public ActivityDueEventHandler(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }
    
    
    @EventListener
    public void on(ActivityDueEvent event) {
        log.info("Handling ActivityDueEvent for activityId: {}, profileId: {}", 
                event.getActivityId(), event.getProfileId());
          try {
            
            log.debug("Using profileId from event: {} for activityId: {}", 
                     event.getProfileId(), event.getActivityId());
            
            
            SendActivityReminderCommand whatsAppCommand = new SendActivityReminderCommand(
                event.getProfileId(),
                event.getActivityId(),
                NotificationChannel.WHATSAPP,
                event.getActivityTitle(),
                String.format("📋 Recordatorio: Tu actividad '%s' está programada para hoy. " +
                             "No olvides completarla para mantener la salud de tus cultivos. 🌱", 
                             event.getActivityTitle())
            );
            
            Long whatsAppNotificationId = notificationCommandService.handle(whatsAppCommand);
            log.info("WhatsApp reminder sent with ID: {} for activity: {}", 
                    whatsAppNotificationId, event.getActivityId());
            
            
            SendActivityReminderCommand emailCommand = new SendActivityReminderCommand(
                event.getProfileId(),
                event.getActivityId(),
                NotificationChannel.EMAIL,
                event.getActivityTitle(),
                String.format("Estimado usuario,\n\n" +
                             "Te recordamos que tienes la actividad '%s' programada para hoy (%s).\n\n" +
                             "Es importante completar esta actividad según lo planificado para asegurar " +
                             "el buen desarrollo de tus cultivos.\n\n" +
                             "Saludos,\nEl equipo de HidroGreen", 
                             event.getActivityTitle(), event.getDueDate().toLocalDate())
            );
            
            Long emailNotificationId = notificationCommandService.handle(emailCommand);
            log.info("Email reminder sent with ID: {} for activity: {}", 
                    emailNotificationId, event.getActivityId());
            
        } catch (Exception e) {
            log.error("Error handling ActivityDueEvent for activityId: {}, profileId: {}: {}", 
                     event.getActivityId(), event.getProfileId(), e.getMessage(), e);
            
        }
    }
}
