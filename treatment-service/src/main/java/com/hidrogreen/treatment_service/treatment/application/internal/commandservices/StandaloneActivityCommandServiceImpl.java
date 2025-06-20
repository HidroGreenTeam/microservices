package com.hidrogreen.treatment_service.treatment.application.internal.commandservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CreateStandaloneActivityCommand;
import com.hidrogreen.treatment_service.treatment.domain.services.StandaloneActivityCommandService;
import com.hidrogreen.treatment_service.treatment.domain.exceptions.ActivityNotFoundException;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.StandaloneActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Standalone activity command service implementation 🆓
 */
@Service
@AllArgsConstructor
public class StandaloneActivityCommandServiceImpl implements StandaloneActivityCommandService {

    private final StandaloneActivityRepository standaloneActivityRepository;

    @Override
    @Transactional
    public Long handle(CreateStandaloneActivityCommand command) {
        // Convert string activityType to ActivityType value object
        var activityType = new com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityType(
            com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityType.Type.valueOf(command.activityType())
        );
        
        // Convert string frequency to ActivityFrequency value object 
        var frequency = new com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityFrequency(
            com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityFrequency.Frequency.valueOf(command.frequency())
        );

        StandaloneActivity activity = new StandaloneActivity(
            command.cropId(),
            command.title(),
            command.description(),
            activityType,
            command.scheduledAt(),
            frequency,
            command.origin()
        );

        // Set optional fields
        if (command.priority() != null) {
            activity.updatePriority(command.priority());
        }

        if (command.instructions() != null) {
            activity.updateInstructions(command.instructions());
        }

        StandaloneActivity savedActivity = standaloneActivityRepository.save(activity);
        return savedActivity.getActivityId();
    }
}
