package com.hidrogreen.treatment_service.diagnosis.infrastructure.clients;

import com.hidrogreen.treatment_service.shared.infrastructure.security.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service", 
    url = "${user.service.url:http://localhost:8081}",
    configuration = FeignConfiguration.class
)
public interface UserServiceClient {

    @GetMapping("/api/v1/user-profiles/internal/users/{id}/exists")
    boolean userExists(@PathVariable Long id);

    // Simplified DTO for internal communication
    record UserExistsInfo(
            Long id,
            boolean exists
    ) {}
} 