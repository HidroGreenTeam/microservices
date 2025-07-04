package com.ayni.notification_service.notifications.interfaces.rest;

import com.ayni.notification_service.notifications.application.internal.outboundservices.EmailNotificationService;
import com.ayni.notification_service.notifications.application.internal.outboundservices.WhatsAppNotificationService;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.domain.services.NotificationQueryService;
import com.ayni.notification_service.notifications.interfaces.rest.resources.NotificationResource;
import com.ayni.notification_service.notifications.interfaces.rest.resources.SendNotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;


@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "🔔 Notification Management - Send emails, WhatsApp messages, and manage notification history (PUBLIC ENDPOINTS)")
public class NotificationsController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

    private final EmailNotificationService emailNotificationService;
    private final WhatsAppNotificationService whatsAppNotificationService;

    public NotificationsController(
            NotificationCommandService notificationCommandService,
            NotificationQueryService notificationQueryService,
            EmailNotificationService emailNotificationService,
            WhatsAppNotificationService whatsAppNotificationService) {
        this.emailNotificationService = emailNotificationService;
        this.whatsAppNotificationService = whatsAppNotificationService;
    }

    
    @PostMapping("/email")
    @Operation(
            summary = "📧 Send email notification", 
            description = "Send an email notification to a user with custom subject and message"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to send email")
    })
    public ResponseEntity<String> sendEmail(@RequestBody SendNotificationResource resource) {
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
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    
    @PostMapping("/whatsapp")
    @Operation(
            summary = "📱 Send WhatsApp notification", 
            description = "Send a WhatsApp message to a user via Twilio integration"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WhatsApp message sent successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to send WhatsApp message")
    })
    public ResponseEntity<String> sendWhatsApp(@RequestBody SendNotificationResource resource) {
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
                    .body("Failed to send WhatsApp message: " + e.getMessage());
        }
    }

    
    @PostMapping("/test/email")
    @Operation(summary = "Test email configuration", description = "Send a test email to verify configuration")
    public ResponseEntity<String> testEmail(@RequestParam String recipient) {
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
                    .body("Failed to send test email: " + e.getMessage());
        }
    }

    
    @PostMapping("/test/whatsapp")
    @Operation(summary = "Test WhatsApp configuration", description = "Send a test WhatsApp message to verify configuration")
    public ResponseEntity<String> testWhatsApp(@RequestParam String recipient) {
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
                    .body("Failed to send test WhatsApp message: " + e.getMessage());
        }
    }

    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Get notification history for a specific user")
    public ResponseEntity<List<NotificationResource>> getUserNotifications(@PathVariable Long userId) {
        try {
            
            logger.info("Getting notifications for user: {}", userId);
            return ResponseEntity.ok().build(); 
        } catch (Exception e) {
            logger.error("Failed to get user notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the notification service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
