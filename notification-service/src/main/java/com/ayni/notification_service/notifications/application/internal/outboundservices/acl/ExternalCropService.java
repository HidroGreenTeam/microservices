package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Service
public class ExternalCropService {

    @Autowired
    private CropServiceClient cropServiceClient;
    
    public record CropInfo(
        Long cropId,
        String cropName,
        String cropType,
        Long profileId,
        String status
    ) {}
    
    
    public Long getCropOwnerProfileId(Long cropId) {
        try {
            var cropDTO = cropServiceClient.getCrop(cropId);
            Long profileId = cropDTO.farmerId();
            return profileId;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve crop owner profile ID", e);
        }
    }
    
    
    public CropInfo getCropInfo(Long cropId) {
        try {
            var cropDTO = cropServiceClient.getCrop(cropId);
            
            CropInfo cropInfo = new CropInfo(
                cropDTO.id(),
                cropDTO.cropName(),
                "Unknown", 
                cropDTO.farmerId(),
                "Active"   
            );
            
            return cropInfo;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve crop information", e);
        }
    }
    
    
    @FeignClient(name = "crop-service", path = "/api/v1", configuration = com.ayni.notification_service.shared.infrastructure.config.FeignConfig.class)
    public interface CropServiceClient {
        
        @GetMapping("/crops/{id}")
        CropDTO getCrop(@PathVariable("id") Long cropId);
        
        record CropDTO(
            Long id,
            String cropName,
            Long area,
            String plantingDate,
            Long farmerId,
            String imageUrl
        ) {}
    }
}
