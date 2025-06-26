package com.ayni.crop_service.crops.application.internal.services;

import com.ayni.crop_service.crops.client.FarmerClient;
import com.ayni.crop_service.shared.domain.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.stereotype.Service;

/**
 * Service for validating external resource IDs from other microservices
 */
@Service
public class ExternalValidationService {

    private final FarmerClient farmerClient;

    public ExternalValidationService(FarmerClient farmerClient) {
        this.farmerClient = farmerClient;
    }

    /**
     * Validates that a farmer exists in the user-service
     * @param farmerId The ID of the farmer to validate
     * @throws ResourceNotFoundException if the farmer doesn't exist
     * @throws RuntimeException if there's a communication error
     */
    public void validateFarmerExists(Long farmerId) {
        try {
            Boolean farmerExists = farmerClient.existsFarmerById(farmerId);
            if (farmerExists == null || !farmerExists) {
                throw new ResourceNotFoundException("Farmer", farmerId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Farmer", farmerId);
        } catch (FeignException e) {
            throw new RuntimeException("Error communicating with user-service to validate farmer: " + e.getMessage());
        }
    }
} 