package com.ayni.notification_service.notifications.domain.services;

import com.ayni.notification_service.notifications.domain.model.commands.ScheduleReminderCommand;

/**
 * ReminderCommandService
 */
public interface ReminderCommandService {
    Long handle(ScheduleReminderCommand command);
    void cancelReminder(Long reminderId);
    void updateReminder(Long reminderId, ScheduleReminderCommand command);
}
