package com.ayni.notification_service.notifications.interfaces.rest;

import com.ayni.notification_service.notifications.application.internal.outboundservices.EmailNotificationService;
import com.ayni.notification_service.notifications.application.internal.outboundservices.WhatsAppNotificationService;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.domain.services.NotificationQueryService;
import com.ayni.notification_service.notifications.interfaces.rest.resources.NotificationResource;
import com.ayni.notification_service.notifications.interfaces.rest.resources.SendNotificationResource;
import com.ayni.notification_service.shared.interfaces.rest.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Notification REST Controller
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Operations related to notifications")
public class NotificationsController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;
    private final EmailNotificationService emailNotificationService;
    private final WhatsAppNotificationService whatsAppNotificationService;

    public NotificationsController(
            NotificationCommandService notificationCommandService,
            NotificationQueryService notificationQueryService,
            EmailNotificationService emailNotificationService,
            WhatsAppNotificationService whatsAppNotificationService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
        this.emailNotificationService = emailNotificationService;
        this.whatsAppNotificationService = whatsAppNotificationService;
    }

    /**
     * Send email notification
     */
    @PostMapping("/email")
    @Operation(summary = "Send email notification", description = "Send an email notification to a user")
    public ResponseEntity<?> sendEmail(@RequestBody SendNotificationResource resource) {
        try {
            logger.info("Sending email notification to: {}", resource.recipient());
            emailNotificationService.sendEmail(
                    resource.recipient(),
                    resource.subject(),
                    resource.message()
            );
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Email sending failed", e.getMessage()));
        }
    }

    /**
     * Send WhatsApp notification
     */
    @PostMapping("/whatsapp")
    @Operation(summary = "Send WhatsApp notification", description = "Send a WhatsApp message to a user")
    public ResponseEntity<?> sendWhatsApp(@RequestBody SendNotificationResource resource) {
        try {
            logger.info("Sending WhatsApp notification to: {}", resource.recipient());
            whatsAppNotificationService.sendWhatsApp(
                    resource.recipient(),
                    resource.message()
            );
            return ResponseEntity.ok("WhatsApp message sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("WhatsApp sending failed", e.getMessage()));
        }
    }

    /**
     * Test email configuration
     */
    @PostMapping("/test/email")
    @Operation(summary = "Test email configuration", description = "Send a test email to verify configuration")
    public ResponseEntity<?> testEmail(@RequestParam String recipient) {
        try {
            logger.info("Testing email configuration with recipient: {}", recipient);
            emailNotificationService.sendEmail(
                    recipient,
                    "Test Email from HidroGreen",
                    "This is a test email to verify the email configuration is working correctly. " +
                    "If you receive this email, the notification service is configured properly."
            );
            return ResponseEntity.ok("Test email sent successfully to " + recipient);
        } catch (Exception e) {
            logger.error("Failed to send test email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Test email failed", e.getMessage()));
        }
    }

    /**
     * Test WhatsApp configuration
     */
    @PostMapping("/test/whatsapp")
    @Operation(summary = "Test WhatsApp configuration", description = "Send a test WhatsApp message to verify configuration")
    public ResponseEntity<?> testWhatsApp(@RequestParam String recipient) {
        try {
            logger.info("Testing WhatsApp configuration with recipient: {}", recipient);
            whatsAppNotificationService.sendWhatsApp(
                    recipient,
                    "🌱 Test message from HidroGreen! This is a test to verify WhatsApp notifications are working correctly."
            );
            return ResponseEntity.ok("Test WhatsApp message sent successfully to " + recipient);
        } catch (Exception e) {
            logger.error("Failed to send test WhatsApp message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Test WhatsApp failed", e.getMessage()));
        }
    }

    /**
     * Get notification history for a user
     */
    @GetMapping("/user/{farmerId}")
    @Operation(summary = "Get user notifications", description = "Get notification history for a specific user")
    public ResponseEntity<?> getUserNotifications(@PathVariable Long farmerId) {
        try {
            // This would be implemented based on your notification query service
            logger.info("Getting notifications for user: {}", farmerId);
            return ResponseEntity.ok().build(); // Placeholder
        } catch (Exception e) {
            logger.error("Failed to get user notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve notifications", e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the notification service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
