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


@Service
@AllArgsConstructor
public class ActivityScheduler {

    private final ActivityRepository activityRepository;
    private final ApplicationEventPublisher eventPublisher;

    
    @Scheduled(fixedRate = 3600000) 
    public void checkOverdueActivities() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<Activity> overdueActivities = activityRepository.findOverdueActivities(currentTime);
            
            for (Activity activity : overdueActivities) {
                if (!activity.getStatus().isOverdue()) {
                    activity.markAsOverdue();
                    activityRepository.save(activity);
                    
                    
                    ActivityOverdueEvent event = new ActivityOverdueEvent(activity);
                    eventPublisher.publishEvent(event);
                }
            }
            
        } catch (Exception e) {
            
        }
    }
}
