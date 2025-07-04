package com.hidrogreen.treatment_service.treatment.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ExternalNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalNotificationService.class);
    
    
    public void sendEmail(String email, String subject, String message) {
        try {
            
            logger.info("📧 Sending email to: {} | Subject: {} | Message: {}", email, subject, message);
            
            
            
            
        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
    
    
    public void sendWhatsApp(String phone, String message) {
        try {
            
            logger.info("📱 Sending WhatsApp to: {} | Message: {}", phone, message);
            
            
            
            
        } catch (Exception e) {
            logger.error("❌ Failed to send WhatsApp to {}: {}", phone, e.getMessage());
            throw new RuntimeException("Failed to send WhatsApp notification", e);
        }
    }
} 