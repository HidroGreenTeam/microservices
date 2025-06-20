package com.hidrogreen.treatment_service.treatment.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * External Notification Service for sending notifications via email and WhatsApp
 */
@Service
public class ExternalNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalNotificationService.class);
    
    /**
     * Send email notification
     * @param email recipient email address
     * @param subject email subject
     * @param message email message content
     */
    public void sendEmail(String email, String subject, String message) {
        try {
            // TODO: Integrate with actual email service (notification-service)
            logger.info("📧 Sending email to: {} | Subject: {} | Message: {}", email, subject, message);
            
            // Here you would integrate with the notification-service microservice
            // For now, we just log the notification
            
        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
    
    /**
     * Send WhatsApp notification
     * @param phone recipient phone number
     * @param message WhatsApp message content
     */
    public void sendWhatsApp(String phone, String message) {
        try {
            // TODO: Integrate with actual WhatsApp service (notification-service)
            logger.info("📱 Sending WhatsApp to: {} | Message: {}", phone, message);
            
            // Here you would integrate with the notification-service microservice
            // For now, we just log the notification
            
        } catch (Exception e) {
            logger.error("❌ Failed to send WhatsApp to {}: {}", phone, e.getMessage());
            throw new RuntimeException("Failed to send WhatsApp notification", e);
        }
    }
} 