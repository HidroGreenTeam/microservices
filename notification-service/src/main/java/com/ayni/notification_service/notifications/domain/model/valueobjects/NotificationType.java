package com.ayni.notification_service.notifications.domain.model.valueobjects;

/**
 * NotificationType enumeration
 */
public enum NotificationType {
    REMINDER,           // Recordatorio programado
    TREATMENT_REMINDER, // Recordatorio de tratamiento
    ACTIVITY_REMINDER,  // Recordatorio de actividad
    ALERT,              // Alerta urgente
    INFO                // Información general
}
