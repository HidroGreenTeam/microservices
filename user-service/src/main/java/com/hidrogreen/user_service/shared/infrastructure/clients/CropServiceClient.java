package com.hidrogreen.user_service.shared.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;


@FeignClient(
    name = "crop-service", 
    configuration = com.hidrogreen.user_service.shared.infrastructure.config.FeignConfig.class
)
public interface CropServiceClient {

    @GetMapping("/api/v1/crops/farmers/{farmerId}/crops")
    List<CropResponse> getCropsFromFarmer(@PathVariable("farmerId") Long farmerId);

    @GetMapping("/api/v1/crops/farmers/{farmerId}/metrics")
    FarmerCropMetrics getFarmerCropMetrics(@PathVariable("farmerId") Long farmerId);

    
    record CropResponse(
        Long id,
        String cropName,
        Long area,
        LocalDate plantingDate,
        Long farmerId,
        String cropImage
    ) {}

    
    record FarmerCropMetrics(
        Long farmerId,
        Integer totalCrops,
        Long totalArea,
        Long averageArea,
        Boolean hasImages
    ) {}
} 