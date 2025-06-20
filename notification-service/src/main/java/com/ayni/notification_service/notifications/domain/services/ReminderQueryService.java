package com.ayni.notification_service.notifications.domain.services;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.domain.model.queries.GetPendingRemindersQuery;

import java.util.List;

/**
 * ReminderQueryService
 */
public interface ReminderQueryService {
    List<Reminder> handle(GetPendingRemindersQuery query);
    List<Reminder> getRemindersByProfileId(Long profileId);
    List<Reminder> getRecurringReminders();
}
