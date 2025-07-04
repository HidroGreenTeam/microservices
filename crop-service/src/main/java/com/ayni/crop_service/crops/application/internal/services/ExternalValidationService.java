package com.ayni.crop_service.crops.application.internal.services;

import com.ayni.crop_service.crops.client.FarmerClient; 
import com.ayni.crop_service.shared.domain.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.stereotype.Service;


@Service
public class ExternalValidationService {

    private final FarmerClient farmerClient;

    public ExternalValidationService(FarmerClient farmerClient) {
        this.farmerClient = farmerClient;
    }

    
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