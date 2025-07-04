package com.hidrogreen.treatment_service.treatment.interfaces.rest.resources;


public record ActivityStatsResponse(
    Long totalActivities,
    Long pendingActivities,
    Long completedActivities,
    Long overdueActivities,
    Long cancelledActivities
) {}
