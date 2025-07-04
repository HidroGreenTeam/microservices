package com.hidrogreen.treatment_service.treatment.application.internal.commandservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CompleteActivityCommand;
import com.hidrogreen.treatment_service.treatment.domain.model.events.ActivityCompletedEvent;
import com.hidrogreen.treatment_service.treatment.domain.services.ActivityCommandService;
import com.hidrogreen.treatment_service.treatment.domain.exceptions.ActivityAlreadyCompletedException;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.ActivityRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class ActivityCommandServiceImpl implements ActivityCommandService {

    private final ActivityRepository activityRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ActivityCommandServiceImpl(ActivityRepository activityRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.activityRepository = activityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<Activity> handle(CompleteActivityCommand command) {
        return activityRepository.findById(command.activityId())
            .map(activity -> {
                if (activity.getStatus().isCompleted()) {
                    throw new ActivityAlreadyCompletedException(command.activityId());
                }
                
                activity.complete();
                
                
                if (command.completionNotes() != null && !command.completionNotes().isBlank()) {
                    
                    
                }
                
                Activity savedActivity = activityRepository.save(activity);
                
                
                ActivityCompletedEvent event = new ActivityCompletedEvent(savedActivity);
                eventPublisher.publishEvent(event);
                
                return savedActivity;
            });
    }

    @Override
    @Transactional
    public Optional<Activity> markAsOverdue(Long activityId) {
        return activityRepository.findById(activityId)
            .map(activity -> {
                activity.markAsOverdue();
                return activityRepository.save(activity);
            });
    }

    @Override
    @Transactional
    public Optional<Activity> cancelActivity(Long activityId, String reason) {
        return activityRepository.findById(activityId)
            .map(activity -> {
                activity.cancel();
                
                activity.updateInstructions("CANCELLED: " + reason);
                return activityRepository.save(activity);
            });
    }
}
