package com.ayni.crop_service.crops.application.internal.commandServiceImpl;

import com.ayni.crop_service.crops.client.FarmerClient;
import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.domain.model.entities.CropImage;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.domain.services.CropImageService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.CropRepository;
import com.ayni.crop_service.shared.domain.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import feign.FeignException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CropCommandServiceImpl implements CropCommandService {
    private final CropRepository cropRepository;
    private final FarmerClient farmerClient;
    private final CropImageService cropImageService;

    @Autowired
    public CropCommandServiceImpl(CropRepository cropRepository, FarmerClient farmerClient, CropImageService cropImageService) {
        this.cropRepository = cropRepository;
        this.farmerClient = farmerClient;
        this.cropImageService = cropImageService;
    }

    @Override
    @Transactional
    public Long handle(CreateCropCommand command, MultipartFile file) throws IOException {
        
        validateCreateCropCommand(command);
        
        
        validateFarmerExists(command.farmerId());

        
        validateCropNamePerFarmer(command.cropName(), command.farmerId());

        
        validateBusinessRules(command);

        CropImage cropImage = null;
        if (file != null && !file.isEmpty()) {
            try {
                cropImage = cropImageService.uploadImage(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload crop image: " + e.getMessage());
            }
        }

        
        Crop crop = new Crop(command);
        crop.setCropImage(cropImage);
        return cropRepository.save(crop).getId();
    }

    @Override
    @Transactional
    public Optional<Crop> handle(UpdateCropCommand command) {
        
        var crop = cropRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Crop", command.id()));

        
        if (command.farmerId() != null && !command.farmerId().equals(crop.getFarmerId())) {
            validateFarmerExists(command.farmerId());
            
            
            if (command.cropName() != null && !command.cropName().equals(crop.getCropName())) {
                validateCropNamePerFarmer(command.cropName(), command.farmerId());
            }
        } else if (command.cropName() != null && !command.cropName().equals(crop.getCropName())) {
            
            validateCropNamePerFarmer(command.cropName(), crop.getFarmerId());
        }

        
        validateUpdateBusinessRules(command, crop);

        var updatedCrop = crop.update(
                command.cropName(),
                command.area(),
                command.plantingDate(),
                command.farmerId()
        );

        return Optional.of(cropRepository.save(updatedCrop));
    }

    @Override
    @Transactional
    public void handle(DeleteCropCommand command) {
        
        var crop = cropRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Crop", command.id()));

        
        
        
        
        if (crop.getCropImage() != null) {
            try {
                cropImageService.deleteImage(crop.getCropImage());
            } catch (IOException e) {
                throw new RuntimeException("Error deleting image from Cloudinary for crop with id " + command.id());
            }
        }

        cropRepository.deleteById(command.id());
    }

    @Override
    @Transactional
    public Optional<Crop> UpdateCropImage(MultipartFile file, Crop crop) throws IOException {
        
        if (crop.getCropImage() != null) {
            cropImageService.deleteImage(crop.getCropImage());
        }

        
        CropImage newImage = cropImageService.uploadImage(file);
        crop.setCropImage(newImage);

        return Optional.of(cropRepository.save(crop));
    }

    @Override
    @Transactional
    public Optional<Crop> deleteCropImage(Long cropId) throws IOException {
        
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop", cropId));

        
        if (crop.getCropImage() == null) {
            throw new IllegalArgumentException("Crop does not have an image to delete");
        }

        
        cropImageService.deleteImage(crop.getCropImage());
        crop.setCropImage(null);
        cropRepository.save(crop);

        return Optional.of(crop);
    }

    
    
    

    private void validateCreateCropCommand(CreateCropCommand command) {
        if (command.cropName() == null || command.cropName().trim().isEmpty()) {
            throw new IllegalArgumentException("Crop name is required");
        }
        
        if (command.area() == null || command.area() <= 0) {
            throw new IllegalArgumentException("Area must be a positive number");
        }
        
        if (command.plantingDate() == null) {
            throw new IllegalArgumentException("Planting date is required");
        }
        
        if (command.farmerId() == null) {
            throw new IllegalArgumentException("Farmer ID is required");
        }
    }

    private void validateFarmerExists(Long farmerId) {
        try {
            Boolean farmerExists = farmerClient.existsFarmerById(farmerId);
            if (farmerExists == null || !farmerExists) {
                throw new ResourceNotFoundException("Farmer", farmerId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Farmer", farmerId);
        } catch (FeignException e) {
            throw new RuntimeException("Error validating farmer existence: " + e.getMessage());
        }
    }

    private void validateCropNamePerFarmer(String cropName, Long farmerId) {
        
        var existingCrops = cropRepository.findCropByFarmerId(farmerId);
        boolean nameExists = existingCrops.stream()
                .anyMatch(crop -> crop.getCropName().equalsIgnoreCase(cropName));
        
        if (nameExists) {
            throw new IllegalArgumentException(
                    String.format("Farmer %d already has a crop named '%s'", farmerId, cropName));
        }
    }

    private void validateBusinessRules(CreateCropCommand command) {
        
        LocalDate today = LocalDate.now();
        LocalDate maxFutureDate = today.plusMonths(3); 
        
        if (command.plantingDate().isAfter(maxFutureDate)) {
            throw new IllegalArgumentException(
                    "Planting date cannot be more than 3 months in the future");
        }
        
        
        if (command.area() > 100000) { 
            throw new IllegalArgumentException(
                    "Area cannot exceed 100,000 square units");
        }

        
        
    }

    private void validateUpdateBusinessRules(UpdateCropCommand command, Crop existingCrop) {
        
        if (command.plantingDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate maxFutureDate = today.plusMonths(3);
            
            if (command.plantingDate().isAfter(maxFutureDate)) {
                throw new IllegalArgumentException(
                        "Planting date cannot be more than 3 months in the future");
            }
        }
        
        if (command.area() != null && command.area() > 100000) {
            throw new IllegalArgumentException(
                    "Area cannot exceed 100,000 square units");
        }
        
        if (command.area() != null && command.area() <= 0) {
            throw new IllegalArgumentException(
                    "Area must be a positive number");
        }
    }
}
