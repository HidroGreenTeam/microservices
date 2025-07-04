package com.ayni.crop_service.crops.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service",
    configuration = com.ayni.crop_service.shared.infrastructure.config.FeignConfig.class
)
public interface FarmerClient {

    @GetMapping("/api/v1/farmers/{farmerId}/exists")
    Boolean existsFarmerById(@PathVariable("farmerId") Long farmerId);
}

