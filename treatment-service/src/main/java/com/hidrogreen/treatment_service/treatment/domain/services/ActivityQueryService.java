package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.queries.*;

import java.util.List;
import java.util.Optional;


public interface ActivityQueryService {

    
    List<Activity> handle(GetActivitiesByCropIdQuery query);

    
    List<Activity> handle(GetTodaysActivitiesQuery query);

    
    List<Activity> handle(GetStandaloneActivitiesQuery query);

    
    List<Activity> handle(GetTreatmentActivitiesQuery query);

    
    Optional<Activity> getActivityById(Long activityId);

    
    List<Activity> getOverdueActivities();

    
    List<Activity> getActivitiesByStatus(String status);
}
