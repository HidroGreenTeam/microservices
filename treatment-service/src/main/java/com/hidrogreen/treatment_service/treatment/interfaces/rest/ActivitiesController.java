package com.hidrogreen.treatment_service.treatment.interfaces.rest;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.GetActivitiesByCropIdQuery;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.GetTodaysActivitiesQuery;
import com.hidrogreen.treatment_service.treatment.domain.services.ActivityQueryService;
import com.hidrogreen.treatment_service.treatment.domain.services.ActivityCommandService;
import com.hidrogreen.treatment_service.treatment.domain.services.StandaloneActivityCommandService;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CompleteActivityCommand;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CreateStandaloneActivityCommand;
import com.hidrogreen.treatment_service.treatment.interfaces.rest.resources.*;
import com.hidrogreen.treatment_service.treatment.application.internal.outboundservices.acl.ExternalNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Activities REST Controller
 */
@RestController
@RequestMapping(value = "/api/v1/activities", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Activities", description = "Agricultural Activities Management")
public class ActivitiesController {

    private static final Logger log = LoggerFactory.getLogger(ActivitiesController.class);

    private final ActivityQueryService activityQueryService;
    private final ActivityCommandService activityCommandService;
    private final StandaloneActivityCommandService standaloneActivityCommandService;
    private final ExternalNotificationService notificationService;

    public ActivitiesController(ActivityQueryService activityQueryService,
                                ActivityCommandService activityCommandService,
                                StandaloneActivityCommandService standaloneActivityCommandService,
                                ExternalNotificationService notificationService) {
        this.activityQueryService = activityQueryService;
        this.activityCommandService = activityCommandService;
        this.standaloneActivityCommandService = standaloneActivityCommandService;
        this.notificationService = notificationService;
    }

    /**
     * 📋 Get all activities
     */
    @GetMapping
    @Operation(summary = "Get all activities", description = "Retrieve all agricultural activities")
    public ResponseEntity<List<ActivityResponse>> getAllActivities(
            @RequestParam(required = false) Long cropId,
            @RequestParam(required = false) String status) {
        
        try {
            List<Activity> activities;
            
            if (cropId != null) {
                var query = new GetActivitiesByCropIdQuery(cropId);
                activities = activityQueryService.handle(query);
            } else {
                // Get today's activities as default
                var query = new GetTodaysActivitiesQuery(LocalDate.now(), null);
                activities = activityQueryService.handle(query);
            }
            
            List<ActivityResponse> activityResponses = activities.stream()
                .map(this::mapToActivityResponse)
                .toList();
            
            return ResponseEntity.ok(activityResponses);
            
        } catch (Exception e) {
            log.error("Error retrieving activities: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 📋 Create new activity
     */
    @PostMapping
    @Operation(summary = "Create activity", description = "Create a new agricultural activity")
    public ResponseEntity<ActivityResponse> createActivity(@RequestBody CreateActivityRequest request) {
        
        try {
            // Create standalone activity command
            var command = new CreateStandaloneActivityCommand(
                request.cropId(),
                request.title(),
                request.description(),
                request.activityType(),
                request.scheduledAt(),
                request.frequency(),
                request.origin() != null ? request.origin() : "USER",
                request.priority() != null ? request.priority() : 2,
                request.instructions()
            );
            
            Long activityId = standaloneActivityCommandService.handle(command);
            
            // Get the created activity to return as response
            Optional<Activity> createdActivity = activityQueryService.getActivityById(activityId);
            
            if (createdActivity.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(
                mapToActivityResponse(createdActivity.get())
            );
            
        } catch (Exception e) {
            log.error("Error creating activity: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Complete an activity
     */
    @PostMapping("/{activityId}/complete")
    @Operation(summary = "Complete activity", description = "Mark an activity as completed")
    public ResponseEntity<ActivityResponse> completeActivity(@PathVariable Long activityId) {
        try {
            CompleteActivityCommand command = new CompleteActivityCommand(activityId, "Completed via API");
            activityCommandService.handle(command);
            
            // Get updated activity
            Optional<Activity> activityOpt = activityQueryService.getActivityById(activityId);
            if (activityOpt.isPresent()) {
                Activity activity = activityOpt.get();
                ActivityResponse response = mapToActivityResponse(activity);
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error completing activity {}: {}", activityId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ⏰ Get overdue activities
     */
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue activities", description = "Get activities that are past their scheduled time")
    public ResponseEntity<List<ActivityResponse>> getOverdueActivities() {
        
        try {
            List<Activity> overdueActivities = activityQueryService.getOverdueActivities();
            
            List<ActivityResponse> activityResponses = overdueActivities.stream()
                .map(this::mapToActivityResponse)
                .toList();
            
            return ResponseEntity.ok(activityResponses);
            
        } catch (Exception e) {
            log.error("Error retrieving overdue activities: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 📊 Get activity statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get activity statistics", description = "Get statistics about activities")
    public ResponseEntity<ActivityStatsResponse> getActivityStats(@RequestParam(required = false) Long cropId) {
        
        try {
            // Get activities for statistics
            List<Activity> activities;
            if (cropId != null) {
                var query = new GetActivitiesByCropIdQuery(cropId);
                activities = activityQueryService.handle(query);
            } else {
                var query = new GetTodaysActivitiesQuery(LocalDate.now(), null);
                activities = activityQueryService.handle(query);
            }
            
            long total = activities.size();
            long pending = activities.stream().filter(a -> "PENDING".equals(a.getStatus().status().name())).count();
            long completed = activities.stream().filter(a -> "COMPLETED".equals(a.getStatus().status().name())).count();
            long overdue = activities.stream().filter(a -> a.getDueDate() != null && a.getDueDate().isBefore(LocalDateTime.now())).count();
            long cancelled = activities.stream().filter(a -> "CANCELLED".equals(a.getStatus().status().name())).count();
            
            ActivityStatsResponse stats = new ActivityStatsResponse(total, pending, completed, overdue, cancelled);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error retrieving activity stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🔔 Send activity reminders
     */
    @PostMapping("/{activityId}/remind")
    @Operation(summary = "Send activity reminder", description = "Send reminder notification for an activity")
    public ResponseEntity<String> sendActivityReminder(
            @PathVariable Long activityId,
            @RequestBody ActivityReminderRequest request) {
        
        try {
            if (request.sendEmail() && request.email() != null) {
                notificationService.sendEmail(request.email(), 
                    "Recordatorio: " + request.activityName(), 
                    "Recordatorio para la actividad '" + request.activityName() + "' en " + request.cropName());
            }
            
            if (request.sendWhatsApp() && request.phone() != null) {
                notificationService.sendWhatsApp(request.phone(), 
                    "Recordatorio: " + request.activityName() + " - " + request.cropName());
            }
            
            return ResponseEntity.ok("Recordatorio enviado exitosamente");
            
        } catch (Exception e) {
            log.error("Error sending activity reminder: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error enviando recordatorio: " + e.getMessage());
        }
    }
    
    /**
     * Map Activity entity to ActivityResponse
     */
    private ActivityResponse mapToActivityResponse(Activity activity) {
        return new ActivityResponse(
            activity.getId(),
            activity.getTitle(),
            activity.getDescription(),
            activity.getActivityType().type().name(),
            activity.getStatus().status().name(),
            activity.getCropId(),
            activity.getScheduledAt(),
            getPriorityString(activity.getPriority()),
            buildNotesString(activity)
        );
    }
    
    private String buildNotesString(Activity activity) {
        if (activity.getNotes() == null || activity.getNotes().isEmpty()) {
            return "";
        }
        return activity.getNotes().stream()
            .map(note -> note.getContent())
            .reduce((a, b) -> a + "; " + b)
            .orElse("");
    }
    
    /**
     * Convert priority number to string
     */
    private String getPriorityString(int priority) {
        return switch (priority) {
            case 1 -> "LOW";
            case 2 -> "MEDIUM";
            case 3 -> "HIGH";
            case 4 -> "URGENT";
            case 5 -> "CRITICAL";
            default -> "MEDIUM";
        };
    }
    
    /**
     * Convert priority string to number
     */
    private Integer getPriorityNumber(String priority) {
        return switch (priority.toUpperCase()) {
            case "LOW" -> 1;
            case "MEDIUM" -> 2;
            case "HIGH" -> 3;
            case "URGENT" -> 4;
            case "CRITICAL" -> 5;
            default -> 2; // Default to MEDIUM
        };
    }
}
