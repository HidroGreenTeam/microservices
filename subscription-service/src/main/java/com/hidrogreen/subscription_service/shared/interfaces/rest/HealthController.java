package com.hidrogreen.subscription_service.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = "*")
@Tag(name = "Health", description = "Health check endpoint")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health Check", description = "Returns the health status of the subscription service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy"),
            @ApiResponse(responseCode = "503", description = "Service is unhealthy")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "subscription-service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
} 