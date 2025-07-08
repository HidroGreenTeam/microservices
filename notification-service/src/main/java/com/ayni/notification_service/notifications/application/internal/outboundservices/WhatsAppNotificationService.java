package com.ayni.notification_service.notifications.application.internal.outboundservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * WhatsApp Notification Service using Twilio
 */
@Service
public class WhatsAppNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationService.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    /**
     * Send WhatsApp message via Twilio
     */
    public void sendWhatsApp(String to, String message) {
        try {
            log.info("=== INITIALIZING TWILIO WHATSAPP SERVICE ===");
            log.info("Account SID: {}", accountSid != null ? accountSid.substring(0, Math.min(10, accountSid.length())) + "..." : "NULL");
            log.info("Auth Token: {}", authToken != null && !authToken.isEmpty() ? "[SET]" : "NOT SET");
            log.info("From Number: {}", fromNumber);
            log.info("To Number: {}", to);
            log.info("Message: {}", message);
            
            // Initialize Twilio
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
            
            log.info("=== SENDING WHATSAPP MESSAGE VIA TWILIO ===");
            
            try {
                // Create WhatsApp message
                Message twilioMessage = Message.creator(
                    new PhoneNumber("whatsapp:" + to),
                    new PhoneNumber(fromNumber),
                    message
                ).create();
                
                log.info("=== WHATSAPP MESSAGE SENT SUCCESSFULLY ===");
                log.info("Message SID: {}", twilioMessage.getSid());
                log.info("Message Status: {}", twilioMessage.getStatus());
                log.info("To: {}", to);
                
            } catch (com.twilio.exception.ApiException apiEx) {
                if (apiEx.getMessage().contains("Channel with the specified From address")) {
                    log.warn("=== WHATSAPP CHANNEL NOT CONFIGURED, FALLING BACK TO SMS ===");
                    log.warn("WhatsApp number {} not configured in Twilio, sending SMS instead", fromNumber);
                    
                    // Fallback to SMS
                    Message smsMessage = Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber(fromNumber.replace("whatsapp:", "")),
                        "SMS: " + message
                    ).create();
                    
                    log.info("=== SMS SENT SUCCESSFULLY AS FALLBACK ===");
                    log.info("SMS SID: {}", smsMessage.getSid());
                    log.info("SMS Status: {}", smsMessage.getStatus());
                } else {
                    throw apiEx;
                }
            }
            
        } catch (Exception e) {
            log.error("=== ERROR SENDING WHATSAPP MESSAGE ===");
            log.error("To: {}, Error: {}", to, e.getMessage(), e);
            log.error("Full stack trace:", e);
            throw new RuntimeException("Failed to send WhatsApp message", e);
        }
    }
}
