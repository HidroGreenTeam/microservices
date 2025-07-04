package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;


@Service
public class ExternalActivityService {
    
    public record ActivityInfo(
        Long activityId,
        String activityName,
        String description,
        Long cropId,
        Long profileId,
        String status
    ) {}
    
    
    public ActivityInfo getActivityInfo(Long activityId) {
        try {
            
            return new ActivityInfo(
                activityId,
                "Mock Activity",
                "Temporary mock activity description",
                1L,
                1L, 
                "PENDING"
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve activity information", e);
        }
    }
    
    
    public Long getActivityOwnerProfileId(Long activityId) {
        try {
            ActivityInfo activityInfo = getActivityInfo(activityId);
            return activityInfo.profileId();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve activity owner profile ID", e);
        }
    }
}
