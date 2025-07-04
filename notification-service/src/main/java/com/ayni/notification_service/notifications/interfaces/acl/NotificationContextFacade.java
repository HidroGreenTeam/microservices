package com.ayni.notification_service.notifications.interfaces.acl;

import java.time.LocalDateTime;


public interface NotificationContextFacade {
    
    
    Long sendNotification(Long profileId, String title, String message, String channel);
    
    
    Long scheduleReminder(Long profileId, String title, String message, 
                         LocalDateTime remindAt, String channel);
    
    
    void sendActivityReminder(Long profileId, Long activityId, String activityTitle);
}
