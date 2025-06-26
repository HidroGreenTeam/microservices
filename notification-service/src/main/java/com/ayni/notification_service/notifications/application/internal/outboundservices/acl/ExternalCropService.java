package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * External Crop Service (ACL) - Real Implementation
 * Anti-Corruption Layer to communicate with Crop Service
 */
@Service
public class ExternalCropService {

    @Autowired
    private CropServiceClient cropServiceClient;
    
    public record CropInfo(
        Long cropId,
        String cropName,
        String cropType,
        Long farmerId,
        String status
    ) {}
    
    /**
     * Gets the profile ID of the crop owner
     * @param cropId The crop ID
     * @return The profile ID of the crop owner
     */
    public Long getCropOwnerfarmerId(Long cropId) {
        try {
            var cropDTO = cropServiceClient.getCrop(cropId);
            Long farmerId = cropDTO.farmerId();
            return farmerId;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve crop owner profile ID", e);
        }
    }
    
    /**
     * Gets basic crop information
     * @param cropId The crop ID
     * @return Crop information
     */
    public CropInfo getCropInfo(Long cropId) {
        try {
            var cropDTO = cropServiceClient.getCrop(cropId);
            
            CropInfo cropInfo = new CropInfo(
                cropDTO.id(),
                cropDTO.name(),
                cropDTO.type(),
                cropDTO.farmerId(),
                cropDTO.status()
            );
            
            return cropInfo;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve crop information", e);
        }
    }
    
    /**
     * Feign Client for Crop Service
     */
    @FeignClient(name = "crop-service", path = "/api/v1")
    public interface CropServiceClient {
        
        @GetMapping("/crops/{id}")
        CropDTO getCrop(@PathVariable("id") Long cropId);
        
        record CropDTO(
            Long id,
            String name,
            String type,
            Long farmerId,
            String status,
            String location,
            java.time.LocalDate plantingDate
        ) {}
    }
}
