package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;

/**
 * ExternalActivityService - Service to interact with Treatment Service
 */
@Service
public class ExternalActivityService {
    
    public record ActivityInfo(
        Long activityId,
        String activityName,
        String description,
        Long cropId,
        Long farmerId,
        String status
    ) {}
    
    /**
     * Gets basic activity information 
     * @param activityId The activity ID
     * @return Activity information  
     */
    public ActivityInfo getActivityInfo(Long activityId) {
        try {
            // Temporary mock response - replace with actual service call when treatment-service is ready
            return new ActivityInfo(
                activityId,
                "Mock Activity",
                "Temporary mock activity description",
                1L,
                1L, // farmerId
                "PENDING"
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve activity information", e);
        }
    }
    
    /**
     * Gets the profile ID of the activity owner
     * @param activityId The activity ID
     * @return The profile ID of the activity owner
     */
    public Long getActivityOwnerfarmerId(Long activityId) {
        try {
            ActivityInfo activityInfo = getActivityInfo(activityId);
            return activityInfo.farmerId();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve activity owner profile ID", e);
        }
    }
}
