package com.hidrogreen.treatment_activity_service.application.services;

import com.hidrogreen.treatment_activity_service.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_activity_service.domain.model.entities.Activity;
import com.hidrogreen.treatment_activity_service.domain.model.valueobjects.ActivityStatus;
import com.hidrogreen.treatment_activity_service.infrastructure.persistence.jpa.repositories.ActivityRepository;
import com.hidrogreen.treatment_activity_service.infrastructure.persistence.jpa.repositories.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final TreatmentRepository treatmentRepository;
    
    @Autowired
    public ActivityService(ActivityRepository activityRepository, TreatmentRepository treatmentRepository) {
        this.activityRepository = activityRepository;
        this.treatmentRepository = treatmentRepository;
    }
    
    public Activity createActivity(Long treatmentId, String name, String description, 
                                   LocalDateTime scheduledDateTime, Integer reminderMinutesBefore) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID: " + treatmentId));
        
        Activity activity = new Activity(name, description, scheduledDateTime, reminderMinutesBefore);
        treatment.addActivity(activity);
        
        return activityRepository.save(activity);
    }
    
    public List<Activity> getActivitiesByTreatmentId(Long treatmentId) {
        return activityRepository.findByTreatmentId(treatmentId);
    }
    
    public List<Activity> getActivitiesByUserId(Long userId) {
        return activityRepository.findByUserId(userId);
    }
    
    public List<Activity> getPendingActivitiesByUserId(Long userId) {
        return activityRepository.findByUserIdAndStatus(userId, ActivityStatus.PENDING);
    }
    
    public List<Activity> getActivitiesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.findActivitiesBetweenDates(startDate, endDate);
    }
    
    public List<Activity> getUserActivitiesBetweenDates(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.findUserActivitiesBetweenDates(userId, startDate, endDate);
    }
    
    public Optional<Activity> getActivityById(Long activityId) {
        return activityRepository.findById(activityId);
    }
    
    public Activity updateActivity(Long activityId, String name, String description, 
                                   LocalDateTime scheduledDateTime, Integer reminderMinutesBefore) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + activityId));
        
        if (name != null && !name.trim().isEmpty()) {
            activity.setName(name);
        }
        if (description != null) {
            activity.setDescription(description);
        }
        if (scheduledDateTime != null) {
            activity.setScheduledDateTime(scheduledDateTime);
            // Reset notification status if date changed
            activity.setNotificationSent(false);
        }
        if (reminderMinutesBefore != null) {
            activity.setReminderMinutesBefore(reminderMinutesBefore);
        }
        
        return activityRepository.save(activity);
    }
    
    public void completeActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + activityId));
        
        activity.complete();
        activityRepository.save(activity);
    }
    
    public void cancelActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + activityId));
        
        activity.cancel();
        activityRepository.save(activity);
    }
    
    public void markActivityInProgress(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + activityId));
        
        activity.markInProgress();
        activityRepository.save(activity);
    }
    
    public void deleteActivity(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new IllegalArgumentException("Actividad no encontrada con ID: " + activityId);
        }
        activityRepository.deleteById(activityId);
    }
    
    public List<Activity> getPendingActivitiesForNotification() {
        return activityRepository.findPendingActivitiesForNotification(LocalDateTime.now());
    }
    
    public void markNotificationSent(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + activityId));
        
        activity.markNotificationSent();
        activityRepository.save(activity);
    }
}
