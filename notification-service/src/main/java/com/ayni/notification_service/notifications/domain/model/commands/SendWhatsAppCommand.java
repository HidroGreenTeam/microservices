package com.ayni.notification_service.notifications.domain.model.commands;


public record SendWhatsAppCommand(
    String to,
    String message
) {
    public SendWhatsAppCommand {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }
    }
}
