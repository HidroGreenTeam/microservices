package com.hidrogreen.treatment_service.diagnosis.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "crop-service", url = "${crop.service.url:http://localhost:8080}")
public interface CropServiceClient {

    @GetMapping("/internal/api/v1/crops/{id}")
    CropInfo getCropById(@PathVariable Long id);

    @GetMapping("/internal/api/v1/crops/{id}/basic")
    BasicCropInfo getBasicCropInfo(@PathVariable Long id);

    // DTOs para comunicación entre microservicios
    record CropInfo(
            Long id,
            String cropName,
            Long area,
            String plantingDate,
            Long farmerId
    ) {}

    record BasicCropInfo(
            Long id,
            String cropName,
            Long area,
            String plantingDate,
            Long farmerId,
            String imageUrl,
            boolean exists
    ) {}
} 