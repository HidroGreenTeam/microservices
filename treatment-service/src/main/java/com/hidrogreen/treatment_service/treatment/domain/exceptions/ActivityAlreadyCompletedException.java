package com.hidrogreen.treatment_service.treatment.domain.exceptions;


public class ActivityAlreadyCompletedException extends RuntimeException {
    public ActivityAlreadyCompletedException(Long activityId) {
        super("Activity with ID " + activityId + " is already completed");
    }
}
