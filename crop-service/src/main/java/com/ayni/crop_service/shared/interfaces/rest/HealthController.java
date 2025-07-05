package com.ayni.crop_service.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Health check endpoint")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Check if the crop service is running")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "crop-service",
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0"
        ));
    }
} 