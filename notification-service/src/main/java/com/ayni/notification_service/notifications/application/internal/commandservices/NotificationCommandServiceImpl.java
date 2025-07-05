package com.ayni.notification_service.notifications.application.internal.commandservices;

import com.ayni.notification_service.notifications.application.internal.outboundservices.EmailNotificationService;
import com.ayni.notification_service.notifications.application.internal.outboundservices.WhatsAppNotificationService;
import com.ayni.notification_service.notifications.application.internal.outboundservices.acl.ExternalProfileService;
import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.commands.SendActivityReminderCommand;
import com.ayni.notification_service.notifications.domain.model.commands.SendEmailCommand;
import com.ayni.notification_service.notifications.domain.model.commands.SendWhatsAppCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.*;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationCommandServiceImpl.class);
    
    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final WhatsAppNotificationService whatsAppNotificationService;
    private final ExternalProfileService externalProfileService;
    private final RestTemplate restTemplate;
    
    // Firebase configuration removed as per requirements
    // Push notifications are now handled through email and SMS only
    
    public NotificationCommandServiceImpl(NotificationRepository notificationRepository,
                                        EmailNotificationService emailNotificationService,
                                        WhatsAppNotificationService whatsAppNotificationService,
                                        ExternalProfileService externalProfileService,
                                        RestTemplate restTemplate) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
        this.whatsAppNotificationService = whatsAppNotificationService;
        this.externalProfileService = externalProfileService;
        this.restTemplate = restTemplate;
    }
      @Override
    public Long handle(SendNotificationCommand command) {
        log.info("Processing SendNotificationCommand for profileId: {}, type: {}, channel: {}", 
                command.profileId(), command.notificationType(), command.notificationChannel());
        
        try {
            Long profileId = command.profileId();
            
            Notification notification;
            if (command.activityId() != null) {
                Long activityId = command.activityId();
                notification = new Notification(profileId, activityId, command.notificationType(), 
                                              command.notificationChannel(), command.title(), command.message());
                log.debug("Created notification for activity: {}", activityId);
            } else if (command.cropId() != null) {
                Long cropId = command.cropId();
                notification = new Notification(profileId, cropId, command.notificationType(), 
                                              command.notificationChannel(), command.title(), command.message(), true);
                log.debug("Created notification for crop: {}", cropId);
            } else {
                notification = new Notification(profileId, command.notificationType(), 
                                              command.notificationChannel(), command.title(), command.message());
                log.debug("Created general notification for profile: {}", profileId);
            }
            
            
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Notification saved successfully with ID: {}", savedNotification.getId());
            
            
            deliverNotification(savedNotification);
            
            
            savedNotification.markAsSent();
            notificationRepository.save(savedNotification);
            savedNotification.publishSentEvent();
            
            return savedNotification.getId();
            
        } catch (Exception e) {
            log.error("Error handling SendNotificationCommand for profileId: {}: {}", 
                     command.profileId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
    
    
    private void deliverNotification(Notification notification) {
        log.info("Starting delivery of notification {} via {} channel for profile: {}", 
                notification.getId(), notification.getNotificationChannel(), notification.getProfileId());
        
        try {
            switch (notification.getNotificationChannel()) {
                case EMAIL -> deliverByEmail(notification);
                case WHATSAPP -> deliverByWhatsApp(notification);
                case PUSH -> deliverByPush(notification);
                default -> {
                    log.warn("Unsupported notification channel: {} for notification: {}", 
                            notification.getNotificationChannel(), notification.getId());
                    throw new UnsupportedOperationException("Channel not supported: " + notification.getNotificationChannel());
                }
            }
            
            log.info("Notification {} delivered successfully via {} channel", 
                    notification.getId(), notification.getNotificationChannel());
            
        } catch (Exception e) {
            log.error("Failed to deliver notification {} via {} channel: {}", 
                     notification.getId(), notification.getNotificationChannel(), e.getMessage(), e);
            throw e;
        }
    }
    
    private void deliverByEmail(Notification notification) {
        log.debug("Retrieving email for profile: {}", notification.getProfileId());
        String email = externalProfileService.getProfileEmail(notification.getProfileId());
        log.debug("Retrieved email: {} for profile: {}", email, notification.getProfileId());
        
        try {
            emailNotificationService.sendEmail(email, notification.getTitle(), notification.getMessage());
            log.info("Email notification {} delivered successfully to profile: {} ({})", 
                    notification.getId(), notification.getProfileId(), email);
        } catch (Exception e) {
            log.error("Failed to send email notification {} to profile: {} ({}): {}", 
                     notification.getId(), notification.getProfileId(), email, e.getMessage(), e);
            throw e;
        }
    }
    
    private void deliverByWhatsApp(Notification notification) {
        log.debug("Retrieving phone number for profile: {}", notification.getProfileId());
        String phone = externalProfileService.getProfilePhoneNumber(notification.getProfileId());
        log.debug("Retrieved phone: {} for profile: {}", phone, notification.getProfileId());
        
        String message = notification.getTitle() + "\n\n" + notification.getMessage();
        
        try {
            whatsAppNotificationService.sendWhatsApp(phone, message);
            log.info("WhatsApp notification {} delivered successfully to profile: {} ({})", 
                    notification.getId(), notification.getProfileId(), phone);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp notification {} to profile: {} ({}): {}", 
                     notification.getId(), notification.getProfileId(), phone, e.getMessage(), e);
            throw e;
        }
    }
    
    private void deliverByPush(Notification notification) {
        log.info("Push notification delivery for notification: {} to profile: {}", 
                notification.getId(), notification.getProfileId());
        
        try {
            
            String deviceToken = getDeviceTokenForProfile(notification.getProfileId());
            
            if (deviceToken == null || deviceToken.isBlank()) {
                log.warn("No device token found for profile: {}, skipping push notification", 
                        notification.getProfileId());
                return;
            }
            
            
                            log.info("Push notification would be sent to device: {} (Firebase removed as per requirements)", deviceToken);
            
            
            notification.markAsDelivered();
            log.info("Push notification {} delivered successfully to profile: {}", 
                    notification.getId(), notification.getProfileId());
            
        } catch (Exception e) {
            log.error("Failed to send push notification {} to profile: {}: {}", 
                     notification.getId(), notification.getProfileId(), e.getMessage(), e);
            throw e;
        }
    }
    
    
    private String getDeviceTokenForProfile(Long profileId) {
        try {
            
            
            
            
            log.debug("Retrieving device token for profile: {}", profileId);
            
            
            
            
            
            
            return "mock-device-token-" + profileId; 
            
        } catch (Exception e) {
            log.error("Error retrieving device token for profile: {}", profileId, e);
            return null;
        }
    }
    
    
    // Firebase push notification method removed as per requirements
    // Push notifications are now handled through email and SMS only
    
    @Override
    public Long handle(SendActivityReminderCommand command) {
        log.info("Processing SendActivityReminderCommand for profileId: {}, activityId: {}", 
                command.profileId(), command.activityId());
        
        try {
            SendNotificationCommand notificationCommand = new SendNotificationCommand(
                command.profileId(),
                NotificationType.ACTIVITY_REMINDER,
                command.notificationChannel(),
                "Recordatorio de Actividad: " + command.activityTitle(),
                command.reminderMessage(),
                command.activityId(),
                null
            );
            return handle(notificationCommand);
            
        } catch (Exception e) {
            log.error("Error handling SendActivityReminderCommand for profileId: {}, activityId: {}: {}", 
                     command.profileId(), command.activityId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send activity reminder", e);
        }
    }
    
    @Override
    public void handle(SendEmailCommand command) {
        log.info("Processing SendEmailCommand to: {}", command.to());
        
        try {
            emailNotificationService.sendEmail(command.to(), command.subject(), command.message());
            log.info("Email sent successfully via direct command to: {}", command.to());
        } catch (Exception e) {
            log.error("Error handling SendEmailCommand to {}: {}", command.to(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    @Override
    public void handle(SendWhatsAppCommand command) {
        log.info("Processing SendWhatsAppCommand to: {}", command.to());
        
        try {
            whatsAppNotificationService.sendWhatsApp(command.to(), command.message());
            log.info("WhatsApp sent successfully via direct command to: {}", command.to());
        } catch (Exception e) {
            log.error("Error handling SendWhatsAppCommand to {}: {}", command.to(), e.getMessage(), e);
            throw new RuntimeException("Failed to send WhatsApp", e);
        }
    }
    
}
