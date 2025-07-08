package com.ayni.crop_service.crops.application.internal.commandservices;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropImageCommand;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.CropRepository;

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
        Crop crop = new Crop(
            command.farmerId(),
            command.cropName(),
            command.area(),
            command.plantingDate(),
            command.location()
        );

        Crop savedCrop = cropRepository.save(crop);
        return savedCrop.getId();
    }

    @Override
    @Transactional
    public Optional<Crop> handle(UpdateCropCommand command) {
        return cropRepository.findById(command.cropId())
            .map(crop -> {
                crop.updateCropName(command.cropName());
                crop.updateArea(command.area());
                crop.updatePlantingDate(command.plantingDate());
                crop.updateLocation(command.location());
                return cropRepository.save(crop);
            });
    }

    @Override
    @Transactional
    public void handle(DeleteCropCommand command) {
        cropRepository.deleteById(command.cropId());
    }

    @Override
    @Transactional
    public Optional<Crop> handle(UpdateCropImageCommand command) {
        return cropRepository.findById(command.cropId())
            .map(crop -> {
                crop.updateImageUrl(command.imageUrl());
                return cropRepository.save(crop);
            });
    }

    @Override
    @Transactional
    public Optional<Crop> removeCropImage(Long cropId) {
        return cropRepository.findById(cropId)
            .map(crop -> {
                crop.removeImage();
                return cropRepository.save(crop);
            });
    }
}
