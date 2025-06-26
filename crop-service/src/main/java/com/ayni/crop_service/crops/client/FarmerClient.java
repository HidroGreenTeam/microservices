package com.ayni.crop_service.crops.client;

import com.ayni.crop_service.shared.infrastructure.security.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service", 
    url = "${user.service.url:http://localhost:8081}",
    configuration = FeignConfiguration.class
)
public interface FarmerClient {

    @GetMapping("/api/v1/user-profiles/internal/farmers/{farmerId}/exists")
    Boolean existsFarmerById(@PathVariable("farmerId") Long farmerId);
}

