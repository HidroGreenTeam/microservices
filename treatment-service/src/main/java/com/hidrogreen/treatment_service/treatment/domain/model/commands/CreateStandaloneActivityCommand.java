package com.hidrogreen.treatment_service.treatment.domain.model.commands;

import java.time.LocalDateTime;

/**
 * Command to create a standalone activity 🆓
 */
public record CreateStandaloneActivityCommand(
    Long cropId,
    String title,
    String description,
    String activityType,
    LocalDateTime scheduledAt,
    String frequency,
    String origin,
    Integer priority,
    String instructions
) {
    public CreateStandaloneActivityCommand {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (activityType == null || activityType.isBlank()) {
            throw new IllegalArgumentException("Activity type cannot be null or blank");
        }
        if (scheduledAt == null) {
            throw new IllegalArgumentException("Scheduled date cannot be null");
        }
        if (frequency == null || frequency.isBlank()) {
            throw new IllegalArgumentException("Frequency cannot be null or blank");
        }
        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin cannot be null or blank");
        }
        if (priority != null && (priority < 1 || priority > 5)) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
    }
}
