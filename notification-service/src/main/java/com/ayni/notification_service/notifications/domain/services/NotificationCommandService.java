package com.ayni.notification_service.notifications.domain.services;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;

/**
 * NotificationCommandService
 */
public interface NotificationCommandService {
    Long handle(SendNotificationCommand command);
}
