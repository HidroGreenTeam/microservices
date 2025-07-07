package com.hidrogreen.treatment_service.shared.infrastructure.scheduling;

import com.hidrogreen.treatment_service.shared.infrastructure.messaging.services.ReminderService;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TreatmentQueryService queryService;
    private final ReminderService reminderService;

    public ReminderScheduler(TreatmentQueryService queryService, ReminderService reminderService) {
        this.queryService = queryService;
        this.reminderService = reminderService;
    }

    @Scheduled(fixedRateString = "${reminder.check.interval:60000}") // default: cada minuto
    public void checkReminders() {
        try {
            logger.debug("Checking for steps that need reminders...");
            
            List<TreatmentStep> stepsNeedingReminder = queryService.getStepsDueForReminder();
            
            if (!stepsNeedingReminder.isEmpty()) {
                logger.info("Found {} steps that need reminders", stepsNeedingReminder.size());
                
                for (TreatmentStep step : stepsNeedingReminder) {
                    if (shouldSendReminder(step)) {
                        reminderService.sendReminder(step);
                    }
                }
            } else {
                logger.debug("No reminders needed at this time");
            }
        } catch (Exception e) {
            logger.error("Error checking reminders", e);
        }
    }

    private boolean shouldSendReminder(TreatmentStep step) {
        if (!step.isHasReminder() || step.getReminderMinutesBefore() == null) {
            return false;
        }

        LocalDateTime reminderTime = step.getScheduledDate()
            .minusMinutes(step.getReminderMinutesBefore());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Verifica si estamos dentro del minuto para enviar el recordatorio
        return now.isAfter(reminderTime) && 
               now.isBefore(reminderTime.plusMinutes(1)) &&
               !step.getStatus().isCompleted() &&
               !step.getStatus().isSkipped();
    }
} 