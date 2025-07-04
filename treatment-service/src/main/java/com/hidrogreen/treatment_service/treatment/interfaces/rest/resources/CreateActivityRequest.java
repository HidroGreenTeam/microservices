package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


public record CreateActivityRequest(
    @NotNull(message = "Crop ID is required")
    Long cropId,
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    String title,
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,
    
    @NotBlank(message = "Activity type is required")
    String activityType,
    
    @NotNull(message = "Scheduled date is required")
    LocalDateTime scheduledAt,
    
    @NotBlank(message = "Frequency is required")
    String frequency,
    
    String origin,
    
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must not exceed 5")
    Integer priority,
    
    @Size(max = 2000, message = "Instructions must not exceed 2000 characters")
    String instructions
) {}
