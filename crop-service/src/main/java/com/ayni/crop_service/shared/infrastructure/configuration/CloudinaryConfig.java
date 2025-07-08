package com.ayni.crop_service.shared.infrastructure.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cloudinary configuration for image upload service
 */
@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "deu4nwmqh",
            "api_key", "789752667392435",
            "api_secret", "QlOIvCICBryMf5qy2HryHoJMpUQ",
            "secure", true
        ));
    }
}
