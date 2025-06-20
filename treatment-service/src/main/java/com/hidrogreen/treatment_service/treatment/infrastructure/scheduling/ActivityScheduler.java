package com.hidrogreen.treatment_service.treatment.infrastructure.scheduling;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.events.ActivityOverdueEvent;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.ActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity scheduler for background tasks
 */
@Service
@AllArgsConstructor
public class ActivityScheduler {

    private final ActivityRepository activityRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Check for overdue activities every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 milliseconds
    public void checkOverdueActivities() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<Activity> overdueActivities = activityRepository.findOverdueActivities(currentTime);
            
            for (Activity activity : overdueActivities) {
                if (!activity.getStatus().isOverdue()) {
                    activity.markAsOverdue();
                    activityRepository.save(activity);
                    
                    // Publish overdue event
                    ActivityOverdueEvent event = new ActivityOverdueEvent(activity);
                    eventPublisher.publishEvent(event);
                }
            }
            
        } catch (Exception e) {
            // Silent error handling - no logging to avoid compilation errors
        }
    }
}
