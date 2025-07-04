package com.hidrogreen.treatment_service.treatment.application.internal.queryservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.*;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityType;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityStatus;
import com.hidrogreen.treatment_service.treatment.domain.services.ActivityQueryService;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.ActivityRepository;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.CropServiceClient;
import com.hidrogreen.treatment_service.shared.domain.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class ActivityQueryServiceImpl implements ActivityQueryService {

    private static final Logger log = LoggerFactory.getLogger(ActivityQueryServiceImpl.class);
    
    private final ActivityRepository activityRepository;
    private final CropServiceClient cropServiceClient;

    @Override
    public Optional<Activity> getActivityById(Long activityId) {
        return activityRepository.findById(activityId);
    }

    @Override
    public List<Activity> handle(GetActivitiesByCropIdQuery query) {
        
        try {
            CropServiceClient.CropDTO crop = cropServiceClient.getCropById(query.getCropId());
            if (crop == null) {
                log.warn("Crop not found with id: {}", query.getCropId());
                throw new ResourceNotFoundException("Crop", query.getCropId());
            }
        } catch (Exception e) {
            log.error("Error validating crop with id: {}: {}", query.getCropId(), e.getMessage());
            throw new ResourceNotFoundException("Crop", query.getCropId());
        }
        
        return activityRepository.findByCropId(query.getCropId());
    }

    @Override
    public List<Activity> handle(GetTodaysActivitiesQuery query) {
        LocalDateTime startOfDay = query.date().atStartOfDay();
        LocalDateTime endOfDay = query.date().atTime(23, 59, 59);
        
        List<Activity> activities = activityRepository.findByScheduledAtBetween(startOfDay, endOfDay);
        
        
        if (query.cropId() != null) {
            
            try {
                CropServiceClient.CropDTO crop = cropServiceClient.getCropById(query.cropId());
                if (crop == null) {
                    log.warn("Crop not found with id: {}", query.cropId());
                    throw new ResourceNotFoundException("Crop", query.cropId());
                }
            } catch (Exception e) {
                log.error("Error validating crop with id: {}: {}", query.cropId(), e.getMessage());
                throw new ResourceNotFoundException("Crop", query.cropId());
            }
            
            return activities.stream()
                .filter(activity -> activity.getCropId().equals(query.cropId()))
                .toList();
        }
        
        return activities;
    }

    @Override
    public List<Activity> handle(GetStandaloneActivitiesQuery query) {
        
        return activityRepository.findAll().stream()
            .filter(activity -> activity.getCropId() != null && 
                   activity.getActivityType().type() == ActivityType.Type.GENERAL)
            .toList();
    }

    @Override
    public List<Activity> handle(GetTreatmentActivitiesQuery query) {
        if (query.getTreatmentId() != null) {
            
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
            return List.of(); 
        }
    }

    
    public List<Activity> getActivitiesByPriority(int priority) {
        return activityRepository.findByPriorityOrderByScheduledAtAsc(priority);
    }
}
