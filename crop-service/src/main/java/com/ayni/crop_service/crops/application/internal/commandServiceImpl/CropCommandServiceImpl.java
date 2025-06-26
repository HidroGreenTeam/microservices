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
import com.ayni.crop_service.shared.domain.exceptions.ValidationException;
import com.ayni.crop_service.shared.domain.exceptions.ExternalServiceException;
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
        // FIXED: Validate business rules
        validateCreateCropCommand(command);
        
        // Validate farmer exists
        validateFarmerExists(command.farmerId());

        // FIXED: Check for duplicate name per farmer (not global)
        validateCropNamePerFarmer(command.cropName(), command.farmerId());

        // Validate business constraints
        validateBusinessRules(command);

        CropImage cropImage = null;
        if (file != null && !file.isEmpty()) {
            try {
                cropImage = cropImageService.uploadImage(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload crop image: " + e.getMessage());
            }
        }

        // Create crop
        Crop crop = new Crop(command);
        crop.setCropImage(cropImage);
        return cropRepository.save(crop).getId();
    }

    @Override
    @Transactional
    public Optional<Crop> handle(UpdateCropCommand command) {
        // Find existing crop
        var crop = cropRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Crop", command.id()));

        // Validate farmer exists if farmerId is being changed
        if (command.farmerId() != null && !command.farmerId().equals(crop.getFarmerId())) {
            validateFarmerExists(command.farmerId());
            
            // FIXED: Validate name uniqueness per NEW farmer if both name and farmer are changing
            if (command.cropName() != null && !command.cropName().equals(crop.getCropName())) {
                validateCropNamePerFarmer(command.cropName(), command.farmerId());
            }
        } else if (command.cropName() != null && !command.cropName().equals(crop.getCropName())) {
            // FIXED: Validate name uniqueness per CURRENT farmer if only name is changing
            validateCropNamePerFarmer(command.cropName(), crop.getFarmerId());
        }

        // Validate business rules for update
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
        // Verify crop exists
        var crop = cropRepository.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Crop", command.id()));

        // TODO: Add validation to check if crop has active diagnoses in treatment-service
        // This should prevent deletion if there are important diagnosis records
        
        // Delete associated image if exists
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
        // Delete existing image if present
        if (crop.getCropImage() != null) {
            cropImageService.deleteImage(crop.getCropImage());
        }

        // Upload new image
        CropImage newImage = cropImageService.uploadImage(file);
        crop.setCropImage(newImage);

        return Optional.of(cropRepository.save(crop));
    }

    @Override
    @Transactional
    public Optional<Crop> deleteCropImage(Long cropId) throws IOException {
        // Find crop
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop", cropId));

        // Verify crop has image
        if (crop.getCropImage() == null) {
            throw new ValidationException("cropImage", null, "Crop does not have an image to delete");
        }

        // Delete image from Cloudinary and database
        cropImageService.deleteImage(crop.getCropImage());
        crop.setCropImage(null);
        cropRepository.save(crop);

        return Optional.of(crop);
    }

    // ============================================
    // VALIDATION METHODS
    // ============================================

    private void validateCreateCropCommand(CreateCropCommand command) {
        if (command.cropName() == null || command.cropName().trim().isEmpty()) {
            throw new ValidationException("cropName", command.cropName(), "Crop name is required");
        }
        
        if (command.area() == null || command.area() <= 0) {
            throw new ValidationException("area", command.area(), "Area must be a positive number");
        }
        
        if (command.plantingDate() == null) {
            throw new ValidationException("plantingDate", null, "Planting date is required");
        }
        
        if (command.farmerId() == null) {
            throw new ValidationException("farmerId", null, "Farmer ID is required");
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
            throw new ExternalServiceException("user-service", "validate farmer existence", e);
        }
    }

    private void validateCropNamePerFarmer(String cropName, Long farmerId) {
        // FIXED: Check for duplicate name per farmer, not globally
        var existingCrops = cropRepository.findCropByFarmerId(farmerId);
        boolean nameExists = existingCrops.stream()
                .anyMatch(crop -> crop.getCropName().equalsIgnoreCase(cropName));
        
        if (nameExists) {
            throw new ValidationException("cropName", cropName, 
                    String.format("Farmer %d already has a crop named '%s'", farmerId, cropName));
        }
    }

    private void validateBusinessRules(CreateCropCommand command) {
        // Validate planting date is not in the future beyond reasonable limits
        LocalDate today = LocalDate.now();
        LocalDate maxFutureDate = today.plusMonths(3); // Allow up to 3 months in future
        
        if (command.plantingDate().isAfter(maxFutureDate)) {
            throw new ValidationException("plantingDate", command.plantingDate(), 
                    "Planting date cannot be more than 3 months in the future");
        }
        
        // Validate reasonable area limits
        if (command.area() > 100000) { // 100,000 square units max
            throw new ValidationException("area", command.area(), 
                    "Area cannot exceed 100,000 square units");
        }

        // TODO: Add validation for farmer's total area limits
        // Could validate against farmer's total farm size from user-service
    }

    private void validateUpdateBusinessRules(UpdateCropCommand command, Crop existingCrop) {
        // Apply same business rules as create if values are being changed
        if (command.plantingDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate maxFutureDate = today.plusMonths(3);
            
            if (command.plantingDate().isAfter(maxFutureDate)) {
                throw new ValidationException("plantingDate", command.plantingDate(), 
                        "Planting date cannot be more than 3 months in the future");
            }
        }
        
        if (command.area() != null && command.area() > 100000) {
            throw new ValidationException("area", command.area(), 
                    "Area cannot exceed 100,000 square units");
        }
        
        if (command.area() != null && command.area() <= 0) {
            throw new ValidationException("area", command.area(), 
                    "Area must be a positive number");
        }
    }
}
