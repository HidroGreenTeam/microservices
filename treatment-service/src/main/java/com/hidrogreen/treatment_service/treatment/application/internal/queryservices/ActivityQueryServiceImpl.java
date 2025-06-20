package com.hidrogreen.treatment_service.treatment.application.internal.queryservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.*;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityType;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityStatus;
import com.hidrogreen.treatment_service.treatment.domain.services.ActivityQueryService;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.ActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Activity query service implementation
 */
@Service
@AllArgsConstructor
public class ActivityQueryServiceImpl implements ActivityQueryService {

    private final ActivityRepository activityRepository;

    @Override
    public Optional<Activity> getActivityById(Long activityId) {
        return activityRepository.findById(activityId);
    }

    @Override
    public List<Activity> handle(GetActivitiesByCropIdQuery query) {
        return activityRepository.findByCropId(query.getCropId());
    }

    @Override
    public List<Activity> handle(GetTodaysActivitiesQuery query) {
        LocalDateTime startOfDay = query.date().atStartOfDay();
        LocalDateTime endOfDay = query.date().atTime(23, 59, 59);
        
        List<Activity> activities = activityRepository.findByScheduledAtBetween(startOfDay, endOfDay);
        
        // Filter by crop ID if provided
        if (query.cropId() != null) {
            return activities.stream()
                .filter(activity -> activity.getCropId().equals(query.cropId()))
                .toList();
        }
        
        return activities;
    }

    @Override
    public List<Activity> handle(GetStandaloneActivitiesQuery query) {
        // Since there's no direct method, we'll get all activities and filter
        return activityRepository.findAll().stream()
            .filter(activity -> activity.getCropId() != null && 
                   activity.getActivityType().type() == ActivityType.Type.GENERAL)
            .toList();
    }

    @Override
    public List<Activity> handle(GetTreatmentActivitiesQuery query) {
        if (query.getTreatmentId() != null) {
            // Since there's no direct method, we'll get all activities and filter
            return activityRepository.findAll().stream()
                .filter(activity -> activity.getActivityType().type() == ActivityType.Type.SPRAYING ||
                                  activity.getActivityType().type() == ActivityType.Type.PEST_CONTROL)
                .toList();
        }
        return activityRepository.findAll().stream()
            .filter(activity -> activity.getActivityType().type() == ActivityType.Type.SPRAYING ||
                              activity.getActivityType().type() == ActivityType.Type.PEST_CONTROL)
            .toList();
    }

    @Override
    public List<Activity> getOverdueActivities() {
        return activityRepository.findOverdueActivities(LocalDateTime.now());
    }

    @Override
    public List<Activity> getActivitiesByStatus(String status) {
        try {
            ActivityStatus.Status statusEnum = ActivityStatus.Status.valueOf(status.toUpperCase());
            ActivityStatus activityStatus = new ActivityStatus(statusEnum);
            return activityRepository.findByStatus(activityStatus);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list for invalid status
        }
    }

    // Additional utility methods
    public List<Activity> getActivitiesByPriority(int priority) {
        return activityRepository.findByPriorityOrderByScheduledAtAsc(priority);
    }
}
