package com.ayni.notification_service.notifications.interfaces.rest.resources;


public record SendNotificationResource(
    Long profileId,
    String recipient,  
    String subject,    
    String title,
    String message,
    String notificationType,
    String notificationChannel,
    Long activityId,
    Long cropId
) {}
