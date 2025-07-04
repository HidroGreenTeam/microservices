package com.hidrogreen.treatment_service.treatment.domain.model.commands;

import java.time.LocalDateTime;


public record CreateTreatmentActivityCommand(
    Long cropId,
    Long treatmentId,
    String title,
    String description,
    String activityType,
    LocalDateTime scheduledAt,
    String frequency,
    Integer treatmentStepOrder,
    Boolean isMandatory,
    String treatmentPhase,
    Integer priority,
    String instructions
) {
    public CreateTreatmentActivityCommand {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
        if (treatmentId == null || treatmentId <= 0) {
            throw new IllegalArgumentException("Treatment ID cannot be null or negative");
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
        if (treatmentStepOrder != null && treatmentStepOrder < 1) {
            throw new IllegalArgumentException("Treatment step order must be positive");
        }
        if (priority != null && (priority < 1 || priority > 5)) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        if (cropId == null || treatmentId == null || title == null || activityType == null || scheduledAt == null || frequency == null) {
            throw new IllegalArgumentException("All required fields must be provided");
        }
    }
}
