package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CompleteActivityCommand;

import java.util.Optional;


public interface ActivityCommandService {

    
    Optional<Activity> handle(CompleteActivityCommand command);

    
    Optional<Activity> markAsOverdue(Long activityId);

    
    Optional<Activity> cancelActivity(Long activityId, String reason);
}
