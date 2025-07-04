package com.hidrogreen.treatment_service.diagnosis.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "crop-service", configuration = com.hidrogreen.treatment_service.shared.infrastructure.config.FeignConfig.class)
public interface CropServiceClient {

    
    @GetMapping("/api/v1/crops/farmers/{farmerId}/crops")
    List<CropDTO> getCropsByFarmerId(@PathVariable Long farmerId);

    
    @GetMapping("/api/v1/crops/{cropId}")
    CropDTO getCropById(@PathVariable Long cropId);
    
    
    record CropDTO(
        Long id,
        String cropName,
        String cropType,
        Long farmerId,
        String status,
        Long area
    ) {}
} 