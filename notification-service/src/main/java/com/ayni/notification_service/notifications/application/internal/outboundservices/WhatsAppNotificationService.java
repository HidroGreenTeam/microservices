package com.ayni.notification_service.notifications.application.internal.outboundservices;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class WhatsAppNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationService.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    
    public void sendWhatsApp(String to, String message) {
        try {
            
            Twilio.init(accountSid, authToken);
            
            log.info("Sending WhatsApp message to: {}", to);
            
            
            Message twilioMessage = Message.creator(
                new PhoneNumber("whatsapp:" + to),
                new PhoneNumber(fromNumber),
                message
            ).create();
            
            log.info("WhatsApp message sent successfully to: {} with SID: {}", to, twilioMessage.getSid());
            
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send WhatsApp message", e);
        }
    }
}
