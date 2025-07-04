package com.ayni.notification_service.notifications.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "treatment-service", path = "/api/v1", configuration = com.ayni.notification_service.shared.infrastructure.config.FeignConfig.class)
public interface TreatmentServiceClient {
    
    @GetMapping("/activities/{id}")
    ActivityDTO getActivity(@PathVariable("id") Long activityId);
    
    @GetMapping("/activities/crop/{cropId}")
    java.util.List<ActivityDTO> getActivitiesByCropId(@PathVariable("cropId") Long cropId);
    
    
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