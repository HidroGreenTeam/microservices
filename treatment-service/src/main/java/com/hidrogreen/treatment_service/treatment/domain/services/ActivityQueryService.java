package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Activity query service interface
 */
public interface ActivityQueryService {

    /**
     * Get all activities by crop ID
     *
     * @param query the query containing the crop ID
     * @return list of activities for the crop
     */
    List<Activity> handle(GetActivitiesByCropIdQuery query);

    /**
     * Get today's activities
     *
     * @param query the query for today's activities
     * @return list of today's activities
     */
    List<Activity> handle(GetTodaysActivitiesQuery query);

    /**
     * Get standalone activities
     *
     * @param query the query for standalone activities
     * @return list of standalone activities
     */
    List<Activity> handle(GetStandaloneActivitiesQuery query);

    /**
     * Get treatment activities
     *
     * @param query the query for treatment activities
     * @return list of treatment activities
     */
    List<Activity> handle(GetTreatmentActivitiesQuery query);

    /**
     * Get activity by ID
     *
     * @param activityId the activity ID
     * @return the activity if found
     */
    Optional<Activity> getActivityById(Long activityId);

    /**
     * Get overdue activities
     *
     * @return list of overdue activities
     */
    List<Activity> getOverdueActivities();

    /**
     * Get activities by status
     *
     * @param status the activity status
     * @return list of activities with the given status
     */
    List<Activity> getActivitiesByStatus(String status);
}
