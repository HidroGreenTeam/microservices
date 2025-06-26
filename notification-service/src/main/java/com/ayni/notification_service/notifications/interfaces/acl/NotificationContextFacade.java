package com.ayni.notification_service.notifications.interfaces.acl;

import java.time.LocalDateTime;

/**
 * NotificationContextFacade
 */
public interface NotificationContextFacade {
    
    /**
     * Send a notification to a user
     * @param farmerId The profile ID
     * @param title The notification title
     * @param message The notification message
     * @param channel The notification channel (EMAIL, WHATSAPP, PUSH)
     * @return The notification ID
     */
    Long sendNotification(Long farmerId, String title, String message, String channel);
    
    /**
     * Schedule a reminder for a user
     * @param farmerId The profile ID
     * @param title The reminder title
     * @param message The reminder message
     * @param remindAt When to send the reminder
     * @param channel The notification channel
     * @return The reminder ID
     */
    Long scheduleReminder(Long farmerId, String title, String message, 
                         LocalDateTime remindAt, String channel);
    
    /**
     * Send an activity reminder
     * @param farmerId The profile ID
     * @param activityId The activity ID
     * @param activityTitle The activity title
     */
    void sendActivityReminder(Long farmerId, Long activityId, String activityTitle);
}
