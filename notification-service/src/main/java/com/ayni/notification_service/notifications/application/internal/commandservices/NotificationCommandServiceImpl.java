package com.ayni.notification_service.notifications.application.internal.commandservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ayni.notification_service.notifications.application.internal.outboundservices.EmailNotificationService;
import com.ayni.notification_service.notifications.application.internal.outboundservices.WhatsAppNotificationService;
import com.ayni.notification_service.notifications.application.internal.outboundservices.acl.ExternalProfileService;
import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;

/**
 * NotificationCommandServiceImpl - Handles notification creation and delivery
 */
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private static final Logger log = LoggerFactory.getLogger(NotificationCommandServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final WhatsAppNotificationService whatsAppNotificationService;
    private final ExternalProfileService externalProfileService;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository,
            EmailNotificationService emailNotificationService,
            WhatsAppNotificationService whatsAppNotificationService,
            ExternalProfileService externalProfileService) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
        this.whatsAppNotificationService = whatsAppNotificationService;
        this.externalProfileService = externalProfileService;
    }

    @Override
    public Long handle(SendNotificationCommand command) {
        log.info("=== PROCESSING SENDNOTIFICATIONCOMMAND ===");
        log.info("Profile ID: {}", command.profileId());
        log.info("Notification Type: {}", command.notificationType());
        log.info("Notification Channel: {}", command.notificationChannel());
        log.info("Title: {}", command.title());
        log.info("Message: {}", command.message());

        try {
            log.info("=== CREATING NOTIFICATION ENTITY ===");
            Notification notification = new Notification(
                    command.profileId(),
                    command.notificationType(),
                    command.notificationChannel(),
                    command.title(),
                    command.message()
            );

            log.info("Notification entity created with ID: {}", notification.getId());

            Notification savedNotification = notificationRepository.save(notification);
            log.info("=== NOTIFICATION SAVED SUCCESSFULLY ===");
            log.info("Saved notification ID: {}", savedNotification.getId());

            log.info("=== MARKING NOTIFICATION AS SENT ===");
            savedNotification.markAsSent();
            notificationRepository.save(savedNotification);
            savedNotification.publishSentEvent();
            
            log.info("=== STARTING NOTIFICATION DELIVERY ===");
            deliverNotification(savedNotification);
            
            log.info("=== NOTIFICATION PROCESS COMPLETED SUCCESSFULLY ===");
            log.info("Final notification ID: {}", savedNotification.getId());
            
            return savedNotification.getId();

        } catch (Exception e) {
            log.error("=== ERROR HANDLING SENDNOTIFICATIONCOMMAND ===");
            log.error("Profile ID: {}, Error: {}", command.profileId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
    /**
     * Deliver notification based on channel
     */
    private void deliverNotification(Notification notification) {
        log.info("Starting delivery of notification {} via {} channel for profile: {}",
                notification.getId(), notification.getNotificationChannel(), notification.getProfileId());

        try {
            switch (notification.getNotificationChannel()) {
                case EMAIL ->
                    deliverByEmail(notification);
                case WHATSAPP ->
                    deliverByWhatsApp(notification);
                default -> {
                    log.warn("Unsupported notification channel: {} for notification: {}",
                            notification.getNotificationChannel(), notification.getId());
                    throw new UnsupportedOperationException("Channel not supported: " + notification.getNotificationChannel());
                }
            }

            log.info("Notification {} delivered successfully via {} channel",
                    notification.getId(), notification.getNotificationChannel());

        } catch (UnsupportedOperationException e) {
            log.error("Failed to deliver notification {} via {} channel: {}",
                    notification.getId(), notification.getNotificationChannel(), e.getMessage(), e);
            throw e;
        }
    }

    private void deliverByEmail(Notification notification) {
        log.debug("Retrieving email for profile: {}", notification.getProfileId());

        try {
            String email = externalProfileService.getProfileEmail(notification.getProfileId());
            log.debug("Retrieved email: {} for profile: {}", email, notification.getProfileId());

            emailNotificationService.sendEmail(email, notification.getTitle(), notification.getMessage());

            notification.markAsDelivered();
            notificationRepository.save(notification);

            log.info("Email notification {} delivered successfully to profile: {} ({})",
                    notification.getId(), notification.getProfileId(), email);

        } catch (Exception e) {
            log.error("Failed to send email notification {} to profile: {}: {}",
                    notification.getId(), notification.getProfileId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deliver notification via WhatsApp
     */
    private void deliverByWhatsApp(Notification notification) {
        log.info("=== STARTING WHATSAPP DELIVERY ===");
        log.info("Notification ID: {}", notification.getId());
        log.info("Profile ID: {}", notification.getProfileId());

        try {
            log.info("=== RETRIEVING PHONE NUMBER FOR PROFILE ===");
            String phone = externalProfileService.getProfilePhoneNumber(notification.getProfileId());
            log.info("Retrieved phone: {} for profile: {}", phone, notification.getProfileId());

            String message = notification.getTitle() + "\n\n" + notification.getMessage();
            log.info("=== PREPARING WHATSAPP MESSAGE ===");
            log.info("Final message to send: {}", message);
            
            log.info("=== CALLING WHATSAPP SERVICE ===");
            whatsAppNotificationService.sendWhatsApp(phone, message);
            
            notification.markAsDelivered();
            notificationRepository.save(notification);
            log.info("=== WHATSAPP NOTIFICATION MARKED AS DELIVERED ===");

            log.info("=== WHATSAPP NOTIFICATION DELIVERED SUCCESSFULLY ===");
            log.info("Notification ID: {}, Profile: {}, Phone: {}", 
                    notification.getId(), notification.getProfileId(), phone);

        } catch (Exception e) {
            log.error("=== ERROR SENDING WHATSAPP NOTIFICATION ===");
            log.error("Notification ID: {}, Profile: {}, Error: {}", 
                    notification.getId(), notification.getProfileId(), e.getMessage(), e);
            throw e;
        }
    }

}
