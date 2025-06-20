package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CompleteActivityCommand;

import java.util.Optional;

/**
 * Activity command service interface
 */
public interface ActivityCommandService {

    /**
     * Complete an activity
     *
     * @param command the complete activity command
     * @return the completed activity
     */
    Optional<Activity> handle(CompleteActivityCommand command);

    /**
     * Mark activity as overdue
     *
     * @param activityId the activity ID
     * @return the updated activity
     */
    Optional<Activity> markAsOverdue(Long activityId);

    /**
     * Cancel an activity
     *
     * @param activityId the activity ID
     * @param reason the cancellation reason
     * @return the cancelled activity
     */
    Optional<Activity> cancelActivity(Long activityId, String reason);
}
