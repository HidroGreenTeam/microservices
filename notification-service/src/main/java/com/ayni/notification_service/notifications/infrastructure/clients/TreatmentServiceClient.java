package com.ayni.notification_service.notifications.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Treatment Service
 * Real integration with treatment-service microservice
 */
@FeignClient(name = "treatment-service", path = "/api/v1")
public interface TreatmentServiceClient {
    
    @GetMapping("/activities/{id}")
    ActivityDTO getActivity(@PathVariable("id") Long activityId);
    
    @GetMapping("/activities/crop/{cropId}")
    java.util.List<ActivityDTO> getActivitiesByCropId(@PathVariable("cropId") Long cropId);
    
    // DTO for Activity information
    record ActivityDTO(
        Long id,
        String title,
        String description,
        String activityType,
        String status,
        Long cropId,
        java.time.LocalDateTime scheduledAt,
        java.time.LocalDateTime dueDate,
        String priority
    ) {}
} 