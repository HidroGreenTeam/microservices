package com.hidrogreen.treatment_service.treatment.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.HashMap;

@Service
public class ExternalNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalNotificationService.class);
    
    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    
    public ExternalNotificationService(
            RestTemplate restTemplate,
            @Value("${notification.service.url:http://notification-service:8084}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }
    
    /**
     * Envía notificación por email con manejo de errores y reintentos
     */
    public boolean sendEmail(String email, String subject, String message) {
        return sendEmailWithRetry(email, subject, message, 3);
    }
    
    /**
     * Envía notificación por WhatsApp con manejo de errores y reintentos
     */
    public boolean sendWhatsApp(String phone, String message) {
        return sendWhatsAppWithRetry(phone, message, 3);
    }
    
    private boolean sendEmailWithRetry(String email, String subject, String message, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("📧 Attempting to send email (attempt {}/{}): {} | Subject: {}", 
                           attempt, maxRetries, email, subject);
                
                // Preparar payload
                Map<String, Object> payload = new HashMap<>();
                payload.put("recipient", email);
                payload.put("subject", subject);
                payload.put("message", message);
                
                // Preparar headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                
                // Hacer llamada al notification-service
                String url = notificationServiceUrl + "/api/v1/notifications/email";
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("✅ Email sent successfully to: {}", email);
                    return true;
                } else {
                    logger.warn("⚠️ Email send failed with status: {} for: {}", response.getStatusCode(), email);
                }
                
            } catch (RestClientException e) {
                logger.error("❌ Failed to send email (attempt {}/{}): {} | Error: {}", 
                           attempt, maxRetries, email, e.getMessage());
                
                if (attempt == maxRetries) {
                    logger.error("❌ All email attempts failed for: {}", email);
                    return false;
                }
                
                // Esperar antes del siguiente intento
                try {
                    Thread.sleep(1000 * attempt); // Backoff exponencial
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
    
    private boolean sendWhatsAppWithRetry(String phone, String message, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("📱 Attempting to send WhatsApp (attempt {}/{}): {}", 
                           attempt, maxRetries, phone);
                
                // Preparar payload
                Map<String, Object> payload = new HashMap<>();
                payload.put("recipient", phone);
                payload.put("message", message);
                
                // Preparar headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                
                // Hacer llamada al notification-service
                String url = notificationServiceUrl + "/api/v1/notifications/whatsapp";
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("✅ WhatsApp sent successfully to: {}", phone);
                    return true;
                } else {
                    logger.warn("⚠️ WhatsApp send failed with status: {} for: {}", response.getStatusCode(), phone);
                }
                
            } catch (RestClientException e) {
                logger.error("❌ Failed to send WhatsApp (attempt {}/{}): {} | Error: {}", 
                           attempt, maxRetries, phone, e.getMessage());
                
                if (attempt == maxRetries) {
                    logger.error("❌ All WhatsApp attempts failed for: {}", phone);
                    return false;
                }
                
                // Esperar antes del siguiente intento
                try {
                    Thread.sleep(1000 * attempt); // Backoff exponencial
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
    
    /**
     * Verifica si el notification-service está disponible
     */
    public boolean isNotificationServiceAvailable() {
        try {
            String url = notificationServiceUrl + "/api/v1/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("⚠️ Notification service not available: {}", e.getMessage());
            return false;
        }
    }
} 