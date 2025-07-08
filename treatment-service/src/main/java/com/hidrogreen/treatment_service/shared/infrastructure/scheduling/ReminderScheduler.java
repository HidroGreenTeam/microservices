package com.hidrogreen.treatment_service.shared.infrastructure.scheduling;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hidrogreen.treatment_service.shared.infrastructure.messaging.services.ReminderService;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentQueryService;

@Component
public class ReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TreatmentQueryService queryService;
    private final ReminderService reminderService;

    public ReminderScheduler(TreatmentQueryService queryService, ReminderService reminderService) {
        this.queryService = queryService;
        this.reminderService = reminderService;
        logger.info("ReminderScheduler initialized successfully");
    }

    @Scheduled(fixedRateString = "${reminder.check.interval:60000}") // default: cada minuto
    @Transactional(readOnly = true)
    public void checkReminders() {
        try {
            logger.info("=== CHECKING REMINDERS AT {} ===", LocalDateTime.now());
            
            List<TreatmentStep> stepsNeedingReminder = queryService.getStepsDueForReminder();
            
            logger.info("Found {} steps that potentially need reminders", stepsNeedingReminder.size());
            
            if (!stepsNeedingReminder.isEmpty()) {
                for (TreatmentStep step : stepsNeedingReminder) {
                    logger.info("Checking step ID: {} - Name: {}", step.getId(), step.getName());
                    logger.info("Step scheduled date: {}", step.getScheduledDate());
                    logger.info("Step has reminder: {}", step.isHasReminder());
                    logger.info("Step reminder minutes before: {}", step.getReminderMinutesBefore());
                    logger.info("Step status: {}", step.getStatus());
                    
                    if (shouldSendReminder(step)) {
                        logger.info("=== SENDING REMINDER FOR STEP {} ===", step.getId());
                        reminderService.sendReminder(step);
                        logger.info("Reminder sent for step {}", step.getId());
                    } else {
                        logger.info("Step {} does not need reminder at this time", step.getId());
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
        logger.info("=== EVALUATING IF SHOULD SEND REMINDER FOR STEP {} ===", step.getId());
        
        if (!step.isHasReminder()) {
            logger.info("Step {} has no reminder enabled", step.getId());
            return false;
        }
        
        if (step.getReminderMinutesBefore() == null) {
            logger.info("Step {} has no reminder minutes configured", step.getId());
            return false;
        }

        LocalDateTime reminderTime = step.getScheduledDate()
            .minusMinutes(step.getReminderMinutesBefore());
        
        LocalDateTime now = LocalDateTime.now();
        
        logger.info("Current time: {}", now);
        logger.info("Step scheduled date: {}", step.getScheduledDate());
        logger.info("Reminder time (scheduled - {} minutes): {}", step.getReminderMinutesBefore(), reminderTime);
        logger.info("Status - Is completed: {}, Is skipped: {}", 
                   step.getStatus().isCompleted(), step.getStatus().isSkipped());
        
        boolean isAfterReminderTime = now.isAfter(reminderTime);
        // Ampliar la ventana de tiempo a 5 minutos para mayor flexibilidad
        boolean isBeforeReminderTimeWindow = now.isBefore(reminderTime.plusMinutes(5));
        boolean isNotCompleted = !step.getStatus().isCompleted();
        boolean isNotSkipped = !step.getStatus().isSkipped();
        
        logger.info("Conditions - isAfterReminderTime: {}, isBeforeReminderTimeWindow: {}, isNotCompleted: {}, isNotSkipped: {}", 
                   isAfterReminderTime, isBeforeReminderTimeWindow, isNotCompleted, isNotSkipped);
        
        boolean shouldSend = isAfterReminderTime && isBeforeReminderTimeWindow && isNotCompleted && isNotSkipped;
        
        logger.info("FINAL DECISION: Should send reminder = {}", shouldSend);
        
        return shouldSend;
    }
}