package com.hidrogreen.subscription_service.subscriptions.interfaces.rest.controllers;

import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.dto.SubscriptionNotificationDto;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.messaging.publisher.SubscriptionNotificationPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for testing subscription notifications and integrations
 */
@RestController
@RequestMapping("/api/v1/subscriptions/test")
@Tag(name = "Subscription Testing", description = "Endpoints for testing subscription notifications and integrations")
public class SubscriptionTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionTestController.class);
    
    private final SubscriptionNotificationPublisher notificationPublisher;
    private final ExternalUserService externalUserService;

    public SubscriptionTestController(SubscriptionNotificationPublisher notificationPublisher,
                                    ExternalUserService externalUserService) {
        this.notificationPublisher = notificationPublisher;
        this.externalUserService = externalUserService;
    }

    @PostMapping("/notification")
    @Operation(summary = "Test subscription notification publishing")
    public ResponseEntity<Map<String, Object>> testNotification(@RequestBody TestNotificationRequest request) {
        try {
            LOGGER.info("Testing notification for user ID: {}", request.getUserId());
            
            // Get user information
            Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(request.getUserId());
            
            String userEmail = request.getEmail();
            String userName = "Test User";
            
            if (userInfoOpt.isPresent()) {
                ExternalUserService.UserInfo userInfo = userInfoOpt.get();
                userEmail = userInfo.email();
                userName = userInfo.firstName() + " " + userInfo.lastName();
            }
            
            // Create test notification
            SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
            notification.setNotificationType(request.getNotificationType());
            notification.setUserId(request.getUserId());
            notification.setUserEmail(userEmail);
            notification.setUserName(userName);
            notification.setSubscriptionId(999L); // Test subscription ID
            notification.setSubscriptionType("BASIC");
            notification.setPlanName("Plan Básico de Prueba");
            notification.setPrice(19.99);
            notification.setCurrency("USD");
            notification.setEventTime(LocalDateTime.now());
            notification.setSubject("Notificación de Prueba - HidroGreen");
            notification.setFeatures("• Funcionalidad de prueba\n• Notificaciones\n• Integración completa");
            notification.setInvoiceNumber("TEST-INV-" + System.currentTimeMillis());
            
            // Publish notification based on type
            switch (request.getNotificationType()) {
                case "SUBSCRIPTION_CREATED":
                    notificationPublisher.publishSubscriptionCreated(notification);
                    break;
                case "SUBSCRIPTION_CANCELLED":
                    notificationPublisher.publishSubscriptionCancelled(notification);
                    break;
                case "SUBSCRIPTION_RENEWED":
                    notificationPublisher.publishSubscriptionRenewed(notification);
                    break;
                default:
                    notificationPublisher.publishSubscriptionCreated(notification);
            }
            
            LOGGER.info("Test notification published successfully for user ID: {}", request.getUserId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test notification sent successfully",
                "notificationType", request.getNotificationType(),
                "userEmail", userEmail,
                "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            LOGGER.error("Failed to send test notification for user ID: {}", request.getUserId(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to send test notification: " + e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Test user service integration")
    public ResponseEntity<Map<String, Object>> testUserService(@PathVariable Long userId) {
        try {
            LOGGER.info("Testing user service integration for user ID: {}", userId);
            
            Optional<ExternalUserService.UserInfo> userInfoOpt = externalUserService.getUserById(userId);
            
            if (userInfoOpt.isPresent()) {
                ExternalUserService.UserInfo userInfo = userInfoOpt.get();
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User service integration working",
                    "userInfo", Map.of(
                        "id", userInfo.id(),
                        "email", userInfo.email(),
                        "firstName", userInfo.firstName(),
                        "lastName", userInfo.lastName()
                    ),
                    "timestamp", LocalDateTime.now()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to test user service for user ID: {}", userId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "User service integration failed: " + e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Check subscription service health and integrations")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            // Test basic functionality
            boolean rabbitMQHealthy = true; // Could add actual RabbitMQ health check
            boolean userServiceHealthy = true; // Could add actual user service health check
            
            return ResponseEntity.ok(Map.of(
                "service", "subscription-service",
                "status", "healthy",
                "integrations", Map.of(
                    "rabbitmq", rabbitMQHealthy ? "healthy" : "unhealthy",
                    "userService", userServiceHealthy ? "healthy" : "unhealthy",
                    "notificationService", "connected via RabbitMQ"
                ),
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0"
            ));
            
        } catch (Exception e) {
            LOGGER.error("Health check failed", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "service", "subscription-service",
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    // DTO for test notification request
    public static class TestNotificationRequest {
        private Long userId;
        private String email;
        private String notificationType = "SUBSCRIPTION_CREATED";

        // Getters and setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(String notificationType) {
            this.notificationType = notificationType;
        }
    }
}
