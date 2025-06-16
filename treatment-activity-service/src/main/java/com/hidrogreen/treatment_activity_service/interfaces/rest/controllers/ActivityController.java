package com.hidrogreen.treatment_activity_service.interfaces.rest.controllers;

import com.hidrogreen.treatment_activity_service.application.services.ActivityService;
import com.hidrogreen.treatment_activity_service.domain.model.entities.Activity;
import com.hidrogreen.treatment_activity_service.interfaces.rest.dto.CreateActivityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activities", description = "Activity management operations")
public class ActivityController {
    
    private final ActivityService activityService;
    
    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    @PostMapping("/treatment/{treatmentId}")
    @Operation(summary = "Create a new activity for a treatment")
    public ResponseEntity<Activity> createActivity(
            @PathVariable Long treatmentId,
            @Valid @RequestBody CreateActivityRequest request) {
        try {
            Activity activity = activityService.createActivity(
                treatmentId,
                request.getName(),
                request.getDescription(),
                request.getScheduledDateTime(),
                request.getReminderMinutesBefore()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/treatment/{treatmentId}")
    @Operation(summary = "Get all activities for a treatment")
    public ResponseEntity<List<Activity>> getActivitiesByTreatmentId(@PathVariable Long treatmentId) {
        List<Activity> activities = activityService.getActivitiesByTreatmentId(treatmentId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all activities for a user")
    public ResponseEntity<List<Activity>> getActivitiesByUserId(@PathVariable Long userId) {
        List<Activity> activities = activityService.getActivitiesByUserId(userId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/user/{userId}/pending")
    @Operation(summary = "Get pending activities for a user")
    public ResponseEntity<List<Activity>> getPendingActivitiesByUserId(@PathVariable Long userId) {
        List<Activity> activities = activityService.getPendingActivitiesByUserId(userId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/user/{userId}/calendar")
    @Operation(summary = "Get user activities between dates (calendar view)")
    public ResponseEntity<List<Activity>> getUserActivitiesBetweenDates(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Activity> activities = activityService.getUserActivitiesBetweenDates(userId, startDate, endDate);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/{activityId}")
    @Operation(summary = "Get activity by ID")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long activityId) {
        return activityService.getActivityById(activityId)
                .map(activity -> ResponseEntity.ok(activity))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{activityId}")
    @Operation(summary = "Update activity")
    public ResponseEntity<Activity> updateActivity(
            @PathVariable Long activityId,
            @Valid @RequestBody CreateActivityRequest request) {
        try {
            Activity activity = activityService.updateActivity(
                activityId,
                request.getName(),
                request.getDescription(),
                request.getScheduledDateTime(),
                request.getReminderMinutesBefore()
            );
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{activityId}/complete")
    @Operation(summary = "Complete activity")
    public ResponseEntity<Void> completeActivity(@PathVariable Long activityId) {
        try {
            activityService.completeActivity(activityId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{activityId}/cancel")
    @Operation(summary = "Cancel activity")
    public ResponseEntity<Void> cancelActivity(@PathVariable Long activityId) {
        try {
            activityService.cancelActivity(activityId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{activityId}/in-progress")
    @Operation(summary = "Mark activity as in progress")
    public ResponseEntity<Void> markActivityInProgress(@PathVariable Long activityId) {
        try {
            activityService.markActivityInProgress(activityId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{activityId}")
    @Operation(summary = "Delete activity")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long activityId) {
        try {
            activityService.deleteActivity(activityId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
