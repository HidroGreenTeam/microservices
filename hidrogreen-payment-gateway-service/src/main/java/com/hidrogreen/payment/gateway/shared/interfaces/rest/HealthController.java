package com.hidrogreen.payment.gateway.shared.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health Controller for Payment Gateway Service
 * Provides health check endpoints for service monitoring
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Health check endpoint
     * @return Service health status
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "hidrogreen-payment-gateway",
            "timestamp", java.time.Instant.now().toString()
        ));
    }
} 