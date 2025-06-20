package com.ayni.notification_service.notifications.domain.model.commands;

/**
 * Command to send email directly
 */
public record SendEmailCommand(
    String to,
    String subject,
    String message
) {
    public SendEmailCommand {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or blank");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }
    }
}
