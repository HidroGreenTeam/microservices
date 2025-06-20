package com.hidrogreen.treatment_service.treatment.domain.exceptions;

/**
 * Exception thrown when an activity is not found
 */
public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(Long activityId) {
        super("Activity with ID " + activityId + " not found");
    }
}
