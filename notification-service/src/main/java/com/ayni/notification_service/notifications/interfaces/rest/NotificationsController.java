package com.ayni.notification_service.notifications.interfaces.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationsByProfileIdQuery;
import com.ayni.notification_service.notifications.domain.services.NotificationQueryService;
import com.ayni.notification_service.notifications.interfaces.rest.resources.NotificationResource;
import com.ayni.notification_service.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Notification REST Controller - Query Only (Read-Only for Frontend)
 * Notifications are created automatically via event processing
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Query notification history and status")
public class NotificationsController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

    private final NotificationQueryService notificationQueryService;

    public NotificationsController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    /**
     * Get notification history for a user (for frontend display)
     */
    @GetMapping("/user/{profileId}")
    @Operation(summary = "Get user notifications", description = "Get notification history for a specific user")
    public ResponseEntity<List<NotificationResource>> getUserNotifications(@PathVariable Long profileId) {
        try {
            logger.info("Getting notifications for profile: {}", profileId);
            
            GetNotificationsByProfileIdQuery query = new GetNotificationsByProfileIdQuery(profileId);
            List<Notification> notifications = notificationQueryService.handle(query);
            
            List<NotificationResource> notificationResources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
                
            return ResponseEntity.ok(notificationResources);
        } catch (Exception e) {
            logger.error("Failed to get user notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get notification by ID (for frontend details)
     */
    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID", description = "Get a specific notification by its ID")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long notificationId) {
        try {
            logger.info("Getting notification: {}", notificationId);

            GetNotificationByIdQuery query = new GetNotificationByIdQuery(notificationId);
            Notification notification = notificationQueryService.handle(query).orElse(null);
            if (notification != null) {
                NotificationResource notificationResource = NotificationResourceFromEntityAssembler.toResourceFromEntity(notification);
                return ResponseEntity.ok(notificationResource);
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
