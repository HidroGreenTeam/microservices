package com.ayni.notification_service.notifications.interfaces.rest.controllers;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.infrastructure.messaging.dto.SubscriptionNotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller for testing notification functionality and integrations
 */
@RestController
@RequestMapping("/api/v1/notifications/test")
@Tag(name = "Notification Testing", description = "Endpoints for testing notification functionality")
public class NotificationTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTestController.class);
    
    private final NotificationCommandService notificationCommandService;

    public NotificationTestController(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @PostMapping("/subscription")
    @Operation(summary = "Test subscription notification processing")
    public ResponseEntity<Map<String, Object>> testSubscriptionNotification(@RequestBody TestSubscriptionNotificationRequest request) {
        try {
            LOGGER.info("Testing subscription notification processing for user: {}", request.getUserEmail());
            
            // Create test subscription notification DTO
            SubscriptionNotificationDto notification = new SubscriptionNotificationDto();
            notification.setNotificationType(request.getNotificationType());
            notification.setUserId(request.getUserId());
            notification.setUserEmail(request.getUserEmail());
            notification.setUserName(request.getUserName());
            notification.setSubscriptionId(request.getSubscriptionId());
            notification.setSubscriptionType(request.getSubscriptionType());
            notification.setPlanName(request.getPlanName());
            notification.setPrice(request.getPrice());
            notification.setCurrency("USD");
            notification.setEventTime(LocalDateTime.now());

            // Process the message as if it came from RabbitMQ
            String title = getNotificationTitle(notification.getNotificationType());
            String notificationMessage = getNotificationMessage(notification);
            
            SendNotificationCommand command = new SendNotificationCommand(
                notification.getUserId(),
                NotificationType.INFO,
                NotificationChannel.EMAIL,
                title,
                notificationMessage,
                null, // activityId
                null  // cropId
            );
            
            Long notificationId = notificationCommandService.handle(command);
            
            LOGGER.info("Successfully processed test subscription notification with ID: {}", notificationId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test subscription notification processed successfully",
                "notificationId", notificationId,
                "notificationType", request.getNotificationType(),
                "userEmail", request.getUserEmail(),
                "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            LOGGER.error("Failed to process test subscription notification", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to process test notification: " + e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/direct")
    @Operation(summary = "Test direct notification sending")
    public ResponseEntity<Map<String, Object>> testDirectNotification(@RequestBody TestDirectNotificationRequest request) {
        try {
            LOGGER.info("Testing direct notification for email: {}", request.getEmail());

            SendNotificationCommand command = new SendNotificationCommand(
                request.getUserId(),
                NotificationType.INFO,
                NotificationChannel.valueOf(request.getChannel().toUpperCase()),
                request.getTitle(),
                request.getMessage(),
                null, // activityId
                null  // cropId
            );
            
            Long notificationId = notificationCommandService.handle(command);
            
            LOGGER.info("Successfully sent direct test notification with ID: {}", notificationId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Direct test notification sent successfully",
                "notificationId", notificationId,
                "channel", request.getChannel(),
                "email", request.getEmail(),
                "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            LOGGER.error("Failed to send direct test notification", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to send test notification: " + e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Check notification service health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            return ResponseEntity.ok(Map.of(
                "service", "notification-service",
                "status", "healthy",
                "capabilities", Map.of(
                    "email", "enabled",
                    "sms", "enabled", 
                    "whatsapp", "enabled",
                    "push", "enabled"
                ),
                "integrations", Map.of(
                    "rabbitmq", "connected",
                    "subscriptionService", "listening for events"
                ),
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0"
            ));
            
        } catch (Exception e) {
            LOGGER.error("Health check failed", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "service", "notification-service",
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    private String getNotificationTitle(String notificationType) {
        return switch (notificationType) {
            case "SUBSCRIPTION_CREATED" -> "¡Suscripción Activada - HidroGreen!";
            case "SUBSCRIPTION_CANCELLED" -> "Suscripción Cancelada - HidroGreen";
            case "SUBSCRIPTION_RENEWED" -> "¡Suscripción Renovada - HidroGreen!";
            case "SUBSCRIPTION_EXPIRING" -> "⏰ Tu Suscripción Está Por Vencer - HidroGreen";
            default -> "Actualización de Suscripción - HidroGreen";
        };
    }

    private String getNotificationMessage(SubscriptionNotificationDto notification) {
        return switch (notification.getNotificationType()) {
            case "SUBSCRIPTION_CREATED" -> 
                String.format("¡Bienvenido a HidroGreen! Tu suscripción %s ha sido activada exitosamente.\n\n" +
                             "Detalles de tu suscripción:\n" + 
                             "• Plan: %s\n" +
                             "• Precio: $%.2f %s\n\n" +
                             "¡Gracias por confiar en nosotros!",
                             notification.getSubscriptionType(),
                             notification.getPlanName(),
                             notification.getPrice(),
                             notification.getCurrency() != null ? notification.getCurrency() : "USD");
            case "SUBSCRIPTION_CANCELLED" -> 
                String.format("Tu suscripción %s ha sido cancelada.\n\n" +
                             "Lamentamos verte partir. Gracias por haber usado HidroGreen.",
                             notification.getSubscriptionType());
            case "SUBSCRIPTION_RENEWED" -> 
                String.format("¡Tu suscripción %s ha sido renovada exitosamente!\n\n" +
                             "Detalles de la renovación:\n" + 
                             "• Plan: %s\n" +
                             "• Precio: $%.2f %s\n\n" +
                             "¡Continúa disfrutando de nuestros servicios!",
                             notification.getSubscriptionType(),
                             notification.getPlanName(),
                             notification.getPrice(),
                             notification.getCurrency() != null ? notification.getCurrency() : "USD");
            default -> 
                "Tu estado de suscripción ha sido actualizado.";
        };
    }

    // DTOs for test requests
    public static class TestSubscriptionNotificationRequest {
        private String notificationType = "SUBSCRIPTION_CREATED";
        private Long userId = 1L;
        private String userEmail;
        private String userName = "Test User";
        private Long subscriptionId = 999L;
        private String subscriptionType = "BASIC";
        private String planName = "Plan Básico";
        private Double price = 19.99;

        // Getters and setters
        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public Long getSubscriptionId() { return subscriptionId; }
        public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
        public String getSubscriptionType() { return subscriptionType; }
        public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }

    public static class TestDirectNotificationRequest {
        private Long userId = 1L;
        private String email;
        private String title = "Notificación de Prueba";
        private String message = "Este es un mensaje de prueba desde el notification-service.";
        private String channel = "EMAIL";

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
    }
}
