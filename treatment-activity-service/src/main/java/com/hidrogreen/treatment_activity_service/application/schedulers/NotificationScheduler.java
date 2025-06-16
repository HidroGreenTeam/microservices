package com.hidrogreen.treatment_activity_service.application.schedulers;

import com.hidrogreen.treatment_activity_service.application.services.ActivityService;
import com.hidrogreen.treatment_activity_service.application.services.TreatmentService;
import com.hidrogreen.treatment_activity_service.domain.model.entities.Activity;
import com.hidrogreen.treatment_activity_service.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_activity_service.infrastructure.messaging.ActivityReminderEvent;
import com.hidrogreen.treatment_activity_service.infrastructure.messaging.NotificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {
    
    private final ActivityService activityService;
    private final TreatmentService treatmentService;
    private final NotificationPublisher notificationPublisher;
    
    @Autowired
    public NotificationScheduler(ActivityService activityService, TreatmentService treatmentService, NotificationPublisher notificationPublisher) {
        this.activityService = activityService;
        this.treatmentService = treatmentService;
        this.notificationPublisher = notificationPublisher;
    }
      @Scheduled(fixedRate = 60000) // Ejecuta cada minuto
    @Transactional
    public void checkPendingNotifications() {
        System.out.println("Checking for pending notifications at: " + LocalDateTime.now());
        
        List<Activity> pendingActivities = activityService.getPendingActivitiesForNotification();
        
        for (Activity activity : pendingActivities) {
            if (activity.shouldSendNotification()) {
                try {
                    // Obtener el tratamiento dentro de la transacción
                    Treatment treatment = treatmentService.getTreatmentById(activity.getTreatmentId()).orElse(null);
                    if (treatment == null) {
                        System.err.println("Treatment not found for activity " + activity.getId());
                        continue;
                    }
                    
                    // Crear evento de recordatorio
                    ActivityReminderEvent event = new ActivityReminderEvent(
                        activity.getId(),
                        treatment.getUserId(),
                        activity.getName(),
                        activity.getDescription(),
                        activity.getScheduledDateTime(),
                        treatment.getName(),
                        "ACTIVITY_REMINDER"
                    );
                    
                    // Publicar evento en RabbitMQ
                    notificationPublisher.publishActivityReminder(event);
                    
                    // Marcar notificación como enviada
                    activityService.markNotificationSent(activity.getId());
                    
                    System.out.println("Notification sent for activity: " + activity.getName() + 
                                     " scheduled at: " + activity.getScheduledDateTime());
                    
                } catch (Exception e) {
                    System.err.println("Error sending notification for activity " + activity.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Scheduled(cron = "0 0 * * * *") // Ejecuta cada hora
    public void checkOverdueActivities() {
        System.out.println("Checking for overdue activities at: " + LocalDateTime.now());
        
        List<Activity> pendingActivities = activityService.getPendingActivitiesForNotification();
        LocalDateTime now = LocalDateTime.now();
        
        for (Activity activity : pendingActivities) {
            if (activity.getScheduledDateTime().isBefore(now)) {
                try {
                    activity.markOverdue();
                    System.out.println("Activity marked as overdue: " + activity.getName());
                } catch (Exception e) {
                    System.err.println("Error marking activity as overdue " + activity.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}
