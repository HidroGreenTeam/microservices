package com.ayni.notification_service.notifications.infrastructure.messaging.listeners;

import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.infrastructure.messaging.dto.SubscriptionNotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener for subscription events from subscription-service
 */
@Component
public class SubscriptionEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionEventListener.class);
    
    private final NotificationCommandService notificationCommandService;
    
    public SubscriptionEventListener(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }
    
    @RabbitListener(queues = "subscription.notification.queue")
    public void handleSubscriptionNotification(SubscriptionNotificationDto notification) {
        try {
            logger.info("Received subscription notification: {}", notification.getNotificationType());
            
            String title = getNotificationTitle(notification.getNotificationType());
            String notificationMessage = getNotificationMessage(notification);
            
            SendNotificationCommand command = new SendNotificationCommand(
                notification.getUserId(),
                NotificationType.SUBSCRIPTION,
                NotificationChannel.WHATSAPP,
                title,
                notificationMessage
            );
            
            Long notificationId = notificationCommandService.handle(command);
            logger.info("Successfully created notification with ID: {} for user: {}", 
                       notificationId, notification.getUserId());
            
        } catch (Exception e) {
            logger.error("Error processing subscription notification: {}", e.getMessage(), e);
        }
    }
    
    private String getNotificationTitle(String notificationType) {
        return switch (notificationType) {
            case "SUBSCRIPTION_CREATED" -> "¡Suscripción Activada - HidroGreen!";
            case "SUBSCRIPTION_ACTIVATED" -> "¡Suscripción Activada - HidroGreen!";
            case "SUBSCRIPTION_CANCELLED" -> "Suscripción Cancelada - HidroGreen";
            case "SUBSCRIPTION_RENEWED" -> "¡Suscripción Renovada - HidroGreen!";
            case "SUBSCRIPTION_EXPIRING" -> "⏰ Tu Suscripción Está Por Vencer - HidroGreen";
            default -> "Actualización de Suscripción - HidroGreen";
        };
    }
    
    private String getNotificationMessage(SubscriptionNotificationDto notification) {
        return switch (notification.getNotificationType()) {
            case "SUBSCRIPTION_CREATED" -> 
                String.format("¡Bienvenido a HidroGreen! Tu suscripción %s ha sido activada exitosamente.\n\n" +
                             "Detalles de tu suscripción:\n" + 
                             "• Plan: %s\n" +
                             "• Precio: $%.2f %s\n\n" +
                             "¡Gracias por confiar en nosotros!",
                             notification.getSubscriptionType(),
                             notification.getPlanName(),
                             notification.getPrice() != null ? notification.getPrice().doubleValue() : 0.0,
                             notification.getCurrency() != null ? notification.getCurrency() : "USD");
            case "SUBSCRIPTION_ACTIVATED" -> 
                String.format("¡Excelente! Tu suscripción %s ha sido activada exitosamente.\n\n" +
                             "Detalles de tu suscripción:\n" + 
                             "• Plan: %s\n" +
                             "• Precio: $%.2f %s\n" +
                             "• Referencia de Pago: %s\n\n" +
                             "¡Ya puedes disfrutar de todas las funcionalidades de tu plan!",
                             notification.getSubscriptionType(),
                             notification.getPlanName(),
                             notification.getPrice() != null ? notification.getPrice().doubleValue() : 0.0,
                             notification.getCurrency() != null ? notification.getCurrency() : "USD",
                             notification.getPaymentReference() != null ? notification.getPaymentReference() : "N/A");
            case "SUBSCRIPTION_CANCELLED" -> 
                String.format("Tu suscripción %s ha sido cancelada.\n\n" +
                             "Lamentamos verte partir. Gracias por haber usado HidroGreen.\n" +
                             "Si cambias de opinión, estaremos aquí para ayudarte.",
                             notification.getSubscriptionType());
            case "SUBSCRIPTION_RENEWED" -> 
                String.format("¡Tu suscripción %s ha sido renovada exitosamente!\n\n" +
                             "Detalles de la renovación:\n" + 
                             "• Plan: %s\n" +
                             "• Precio: $%.2f %s\n\n" +
                             "¡Continúa disfrutando de nuestros servicios!",
                             notification.getSubscriptionType(),
                             notification.getPlanName(),
                             notification.getPrice() != null ? notification.getPrice().doubleValue() : 0.0,
                             notification.getCurrency() != null ? notification.getCurrency() : "USD");
            case "SUBSCRIPTION_EXPIRING" -> 
                String.format("Tu suscripción %s está por vencer pronto.\n\n" +
                             "No te quedes sin acceso a nuestros servicios. Renueva tu suscripción para continuar disfrutando de nuestros servicios.\n\n" +
                             "¡Renueva ahora y mantén tu cuenta activa!",
                             notification.getSubscriptionType());
            default -> 
                "Tu estado de suscripción ha sido actualizado.";
        };
    }
}
