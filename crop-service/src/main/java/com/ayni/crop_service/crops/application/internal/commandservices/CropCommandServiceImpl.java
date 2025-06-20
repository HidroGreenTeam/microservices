package com.ayni.crop_service.crops.application.internal.commandservices;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropStatusCommand;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.CropRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Crop command service implementation
 */
@Service
public class CropCommandServiceImpl implements CropCommandService {

    private final CropRepository cropRepository;

    public CropCommandServiceImpl(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    @Override
    @Transactional
    public Long handle(CreateCropCommand command) {
        String cropName = command.cropName();

        Crop crop = new Crop(
            command.getProfileId(),
            cropName,
            command.plantingDate(),
            command.location()
        );

        if (command.notes() != null && !command.notes().isBlank()) {
            crop.updateNotes(command.notes());
        }

        Crop savedCrop = cropRepository.save(crop);
        return savedCrop.getId();
    }

    @Override
    @Transactional
    public Optional<Crop> handle(UpdateCropStatusCommand command) {
        return cropRepository.findById(command.cropId())
            .map(crop -> {
                crop.updateHealthStatus(command.healthStatus());
                if (command.notes() != null && !command.notes().isBlank()) {
                    crop.updateNotes(command.notes());
                }
                return cropRepository.save(crop);
            });
    }

    @Override
    @Transactional
    public Optional<Crop> updateCropNotes(Long cropId, String notes) {
        return cropRepository.findById(cropId)
            .map(crop -> {
                crop.updateNotes(notes);
                return cropRepository.save(crop);
            });
    }
}
