package com.ayni.notification_service.notifications.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url:http://localhost:8081}")
public interface ProfileServiceClient {
    
    @GetMapping("/api/v1/profiles/{profileId}/phone")
    String getPhoneNumber(@PathVariable("profileId") Long profileId);
} 