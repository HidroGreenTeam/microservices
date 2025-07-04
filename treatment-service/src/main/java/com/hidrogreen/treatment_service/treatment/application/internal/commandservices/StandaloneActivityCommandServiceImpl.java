package com.hidrogreen.treatment_service.treatment.application.internal.commandservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import com.hidrogreen.treatment_service.treatment.domain.model.commands.CreateStandaloneActivityCommand;
import com.hidrogreen.treatment_service.treatment.domain.services.StandaloneActivityCommandService;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.StandaloneActivityRepository;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.CropServiceClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@AllArgsConstructor
public class StandaloneActivityCommandServiceImpl implements StandaloneActivityCommandService {

    private static final Logger log = LoggerFactory.getLogger(StandaloneActivityCommandServiceImpl.class);
    
    private final StandaloneActivityRepository standaloneActivityRepository;
    private final CropServiceClient cropServiceClient;

    @Override
    @Transactional
    public Long handle(CreateStandaloneActivityCommand command) {
        
        try {
            CropServiceClient.CropDTO crop = cropServiceClient.getCropById(command.cropId());
            if (crop == null) {
                log.warn("Crop not found with id: {}", command.cropId());
                throw new IllegalArgumentException("Crop not found with id: " + command.cropId());
            }
        } catch (Exception e) {
            log.error("Error validating crop with id: {}: {}", command.cropId(), e.getMessage());
            throw new IllegalArgumentException("Crop not found with id: " + command.cropId());
        }
        
        
        var activityType = new com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityType(
            com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityType.Type.valueOf(command.activityType())
        );
        
        
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
