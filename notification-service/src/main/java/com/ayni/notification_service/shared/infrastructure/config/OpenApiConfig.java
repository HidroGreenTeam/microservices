package com.ayni.notification_service.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HidroGreen Notification Service API")
                        .version("v1.0")
                        .description("API for managing notifications, emails, WhatsApp messages, and reminders in the HidroGreen ecosystem")
                        .contact(new Contact()
                                .name("HidroGreen Development Team")
                                .email("dev@hidrogreen.com")
                                .url("https://hidrogreen.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8084")
                                .description("Development server"),
                        new Server()
                                .url("https://api.hidrogreen.com/notifications")
                                .description("Production server")));
    }
} 