package com.ayni.notification_service.notifications.application.acl;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.commands.ScheduleReminderCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.domain.services.ReminderCommandService;
import com.ayni.notification_service.notifications.interfaces.acl.NotificationContextFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * NotificationContextFacadeImpl
 */
@Service
public class NotificationContextFacadeImpl implements NotificationContextFacade {
    
    private final NotificationCommandService notificationCommandService;
    private final ReminderCommandService reminderCommandService;
    
    public NotificationContextFacadeImpl(NotificationCommandService notificationCommandService,
                                       ReminderCommandService reminderCommandService) {
        this.notificationCommandService = notificationCommandService;
        this.reminderCommandService = reminderCommandService;
    }
    
    @Override
    public Long sendNotification(Long farmerId, String title, String message, String channel) {
        NotificationChannel notificationChannel = NotificationChannel.valueOf(channel.toUpperCase());
        SendNotificationCommand command = new SendNotificationCommand(
            farmerId,
            NotificationType.INFO,
            notificationChannel,
            title,
            message
        );
        return notificationCommandService.handle(command);
    }
    
    @Override
    public Long scheduleReminder(Long farmerId, String title, String message, 
                               LocalDateTime remindAt, String channel) {
        NotificationChannel notificationChannel = NotificationChannel.valueOf(channel.toUpperCase());
        ScheduleReminderCommand command = new ScheduleReminderCommand(
            farmerId,
            notificationChannel,
            title,
            message,
            remindAt
        );
        return reminderCommandService.handle(command);
    }
    
    @Override
    public void sendActivityReminder(Long farmerId, Long activityId, String activityTitle) {
        SendNotificationCommand command = new SendNotificationCommand(
            farmerId,
            NotificationType.REMINDER,
            NotificationChannel.WHATSAPP,
            "Recordatorio: " + activityTitle,
            "No olvides completar tu actividad programada: " + activityTitle,
            activityId,
            null
        );
        notificationCommandService.handle(command);
    }
}
