package com.ayni.notification_service.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.Properties;

/**
 * Configuration for notification services (Email, SMS, Push Notifications)
 */
@Configuration
public class NotificationConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationConfig.class);
    
    @Value("${spring.mail.host}")
    private String mailHost;
    
    @Value("${spring.mail.port}")
    private int mailPort;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.password}")
    private String mailPassword;
    
    @Value("${twilio.account-sid}")
    private String twilioAccountSid;
    
    @Value("${twilio.auth-token}")
    private String twilioAuthToken;
    
    @Value("${twilio.whatsapp.from}")
    private String twilioWhatsAppFrom;
    
    @PostConstruct
    public void validateConfiguration() {
        logger.info("=== Notification Service Configuration ===");
        logger.info("Mail Host: {}", mailHost);
        logger.info("Mail Port: {}", mailPort);
        logger.info("Mail Username: {}", mailUsername != null ? mailUsername : "NOT SET");
        logger.info("Mail Password: {}", mailPassword != null && !mailPassword.isEmpty() ? "[SET]" : "NOT SET");
        logger.info("Twilio Account SID: {}", twilioAccountSid != null ? twilioAccountSid.substring(0, Math.min(10, twilioAccountSid.length())) + "..." : "NOT SET");
        logger.info("Twilio Auth Token: {}", twilioAuthToken != null && !twilioAuthToken.isEmpty() ? "[SET]" : "NOT SET");
        logger.info("Twilio WhatsApp From: {}", twilioWhatsAppFrom != null ? twilioWhatsAppFrom : "NOT SET");
        logger.info("==========================================");
    }
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "false");
        
        logger.info("JavaMailSender configured successfully");
        return mailSender;
    }
}
