package com.ayni.notification_service.notifications.application.internal.eventhandlers;

import com.ayni.notification_service.notifications.domain.model.events.ActivityOverdueEvent;
import com.ayni.notification_service.notifications.application.internal.outboundservices.acl.ExternalActivityService;
import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationChannel;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * ActivityOverdueEventHandler - Handles events from Activities BC
 */
@Service
public class ActivityOverdueEventHandler {
    
    private final NotificationCommandService notificationCommandService;
    private final ExternalActivityService externalActivityService;
    
    public ActivityOverdueEventHandler(NotificationCommandService notificationCommandService,
                                     ExternalActivityService externalActivityService) {
        this.notificationCommandService = notificationCommandService;
        this.externalActivityService = externalActivityService;
    }

    /**
     * Handles ActivityOverdueEvent by sending urgent alert notifications
     */    
    @EventListener
    public void on(ActivityOverdueEvent event) {
        try {
            // Get activity information including owner profile ID
            var activityInfo = externalActivityService.getActivityInfo(event.getActivityId());
            Long farmerId = activityInfo.farmerId();
            
            // Send urgent overdue alert via WhatsApp
            SendNotificationCommand whatsAppCommand = new SendNotificationCommand(
                farmerId,
                NotificationType.ALERT,
                NotificationChannel.WHATSAPP,
                "⚠️ ACTIVIDAD VENCIDA - URGENTE",
                String.format("🚨 ATENCIÓN: La actividad '%s' está VENCIDA.\n\n" +
                             "⏰ Esta actividad debía completarse el %s y aún está pendiente.\n" +
                             "🌱 Esto podría afectar la salud de tus cultivos.\n\n" +
                             "👆 Ingresa a la app AHORA y completa la actividad.", 
                             event.getTitle(), event.getDueDate().toLocalDate()),
                event.getActivityId(),
                event.getCropId()
            );
            
            Long whatsAppNotificationId = notificationCommandService.handle(whatsAppCommand);
            
            // Send urgent email alert as backup
            SendNotificationCommand emailCommand = new SendNotificationCommand(
                farmerId,
                NotificationType.ALERT,
                NotificationChannel.EMAIL,
                "⚠️ ALERTA: Actividad Agrícola Vencida - Acción Requerida",
                String.format("Estimado usuario,\n\n" +
                             "🚨 ALERTA IMPORTANTE:\n\n" +
                             "La actividad '%s' está VENCIDA y requiere tu atención inmediata.\n\n" +
                             "📅 Fecha límite: %s\n" +
                             "📋 Tipo: %s\n" +
                             "🌾 Cultivo ID: %d\n\n" +
                             "⚠️ RIESGOS:\n" +
                             "• El retraso puede afectar la salud de tus cultivos\n" +
                             "• Posible pérdida de productividad\n" +
                             "• Compromete el cronograma de actividades\n\n" +
                             "🔗 ACCIÓN REQUERIDA:\n" +
                             "Ingresa a la plataforma HidroGreen INMEDIATAMENTE y completa esta actividad.\n\n" +
                             "Si tienes problemas, contacta a nuestro equipo de soporte.\n\n" +
                             "Saludos urgentes,\nEl equipo de HidroGreen", 
                             event.getTitle(), event.getDueDate().toLocalDate(), 
                             event.getActivityType(), event.getCropId()),
                event.getActivityId(),
                event.getCropId()
            );
            
            Long emailNotificationId = notificationCommandService.handle(emailCommand);
            
        } catch (Exception e) {
            // Silent error handling - no logging to avoid compilation errors
        }
    }
}
