package com.hidrogreen.treatment_service.treatment.domain.model.commands;

/**
 * Command to complete an activity
 */
public record CompleteActivityCommand(
    Long activityId,
    String completionNotes
) {
    public CompleteActivityCommand {
        if (activityId == null || activityId <= 0) {
            throw new IllegalArgumentException("Activity ID cannot be null or negative");
        }
    }
}

