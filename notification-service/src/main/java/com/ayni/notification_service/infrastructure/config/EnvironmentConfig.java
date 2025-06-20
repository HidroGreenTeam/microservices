package com.ayni.notification_service.infrastructure.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to load environment variables from .env file
 */
@Configuration
public class EnvironmentConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    
    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // Try to load .env file from the current directory
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();
            
            // Set environment variables as system properties
            setEnvVar(dotenv, "EMAIL_USERNAME");
            setEnvVar(dotenv, "EMAIL_PASSWORD");
            setEnvVar(dotenv, "EMAIL_FROM_ADDRESS");
            setEnvVar(dotenv, "TWILIO_ACCOUNT_SID");
            setEnvVar(dotenv, "TWILIO_AUTH_TOKEN");
            setEnvVar(dotenv, "TWILIO_WHATSAPP_FROM");
            setEnvVar(dotenv, "FIREBASE_SERVER_KEY");
            
            logger.info("Environment variables loaded successfully from .env file");
            
        } catch (Exception e) {
            logger.warn("Could not load .env file: {}. Using default values or system environment variables.", e.getMessage());
        }
    }
    
    private void setEnvVar(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null && !value.trim().isEmpty()) {
            System.setProperty(key, value);
            logger.debug("Loaded environment variable: {} = [HIDDEN]", key);
        }
    }
}
