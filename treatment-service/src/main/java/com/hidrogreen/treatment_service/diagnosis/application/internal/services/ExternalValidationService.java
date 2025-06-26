package com.hidrogreen.treatment_service.diagnosis.application.internal.services;

import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.CropServiceClient;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.UserServiceClient;
import com.hidrogreen.treatment_service.shared.domain.exceptions.ExternalServiceException;
import com.hidrogreen.treatment_service.shared.domain.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.stereotype.Service;

/**
 * Service for validating external resource IDs from other microservices
 */
@Service
public class ExternalValidationService {

    private final CropServiceClient cropServiceClient;
    private final UserServiceClient userServiceClient;

    public ExternalValidationService(CropServiceClient cropServiceClient, 
                                   UserServiceClient userServiceClient) {
        this.cropServiceClient = cropServiceClient;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Validates that a crop exists in the crop-service
     * @param cropId The ID of the crop to validate
     * @throws ResourceNotFoundException if the crop doesn't exist
     * @throws ExternalServiceException if there's a communication error
     */
    public void validateCropExists(Long cropId) {
        try {
            var cropInfo = cropServiceClient.getBasicCropInfo(cropId);
            if (!cropInfo.exists()) {
                throw new ResourceNotFoundException("Crop", cropId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Crop", cropId);
        } catch (FeignException ex) {
            throw new ExternalServiceException("crop-service", "validate crop existence", ex);
        }
    }

    /**
     * Validates that a user exists in the user-service
     * @param userId The ID of the user to validate
     * @throws ResourceNotFoundException if the user doesn't exist
     * @throws ExternalServiceException if there's a communication error
     */
    public void validateUserExists(Long userId) {
        try {
            boolean userExists = userServiceClient.userExists(userId);
            if (!userExists) {
                throw new ResourceNotFoundException("User", userId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("User", userId);
        } catch (FeignException ex) {
            throw new ExternalServiceException("user-service", "validate user existence", ex);
        }
    }

    /**
     * Validates multiple external resources at once
     * @param cropId The crop ID to validate (optional)
     * @param userId The user ID to validate (optional)
     */
    public void validateExternalResources(Long cropId, Long userId) {
        if (cropId != null) {
            validateCropExists(cropId);
        }
        
        if (userId != null) {
            validateUserExists(userId);
        }
    }

    /**
     * Gets basic crop info for validation purposes
     * @param cropId The ID of the crop
     * @return Basic crop information
     */
    public CropServiceClient.BasicCropInfo getCropInfo(Long cropId) {
        try {
            return cropServiceClient.getBasicCropInfo(cropId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Crop", cropId);
        } catch (FeignException ex) {
            throw new ExternalServiceException("crop-service", "get crop info", ex);
        }
    }
} 