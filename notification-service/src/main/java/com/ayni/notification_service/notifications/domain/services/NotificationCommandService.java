package com.ayni.notification_service.notifications.domain.services;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.commands.SendActivityReminderCommand;
import com.ayni.notification_service.notifications.domain.model.commands.SendEmailCommand;
import com.ayni.notification_service.notifications.domain.model.commands.SendWhatsAppCommand;

/**
 * NotificationCommandService
 */
public interface NotificationCommandService {
    Long handle(SendNotificationCommand command);
    Long handle(SendActivityReminderCommand command);
    void handle(SendEmailCommand command);
    void handle(SendWhatsAppCommand command);
}
