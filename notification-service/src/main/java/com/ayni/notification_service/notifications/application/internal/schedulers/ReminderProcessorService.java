package com.ayni.notification_service.notifications.application.internal.schedulers;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.domain.model.commands.SendNotificationCommand;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationType;
import com.ayni.notification_service.notifications.domain.services.NotificationCommandService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReminderProcessorService - Handles the processing of due reminders
 */
@Service
public class ReminderProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReminderProcessorService.class);
    
    private final ReminderRepository reminderRepository;
    private final NotificationCommandService notificationCommandService;
    
    public ReminderProcessorService(ReminderRepository reminderRepository,
                                   NotificationCommandService notificationCommandService) {
        this.reminderRepository = reminderRepository;
        this.notificationCommandService = notificationCommandService;
    }
    
    /**
     * Process due reminders - runs every minute
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        logger.debug("Starting reminder processing cycle at: {}", now);
        
        try {
            List<Reminder> dueReminders = reminderRepository.findByRemindAtBeforeAndIsActive(now, true);
            
            if (dueReminders.isEmpty()) {
                logger.debug("No due reminders found at: {}", now);
                return;
            }
            
            logger.info("Processing {} due reminders at: {}", dueReminders.size(), now);
            
            int successCount = 0;
            int errorCount = 0;
            
            for (Reminder reminder : dueReminders) {
                try {
                    logger.debug("Processing reminder ID: {} for profile: {} due at: {}", 
                             reminder.getId(), reminder.getfarmerId(), reminder.getRemindAt());
                    
                    // Create notification command
                    SendNotificationCommand command = new SendNotificationCommand(
                        reminder.getfarmerId(),
                        NotificationType.REMINDER,
                        reminder.getNotificationChannel(),
                        reminder.getTitle(),
                        reminder.getMessage(),
                        reminder.getActivityId(),
                        reminder.getCropId()
                    );
                    
                    // Send notification via command service
                    Long notificationId = notificationCommandService.handle(command);
                    logger.info("Delivered notification {} for reminder ID: {} to profile: {}", 
                            notificationId, reminder.getId(), reminder.getfarmerId());
                    
                    // Handle reminder recurrence
                    if (!reminder.isRecurring()) {
                        reminder.deactivate();
                        reminderRepository.save(reminder);
                        logger.info("Reminder {} deactivated after sending", reminder.getId());
                    } else {
                        // Update next reminder time based on pattern
                        updateRecurringReminder(reminder);
                        logger.info("Recurring reminder {} updated for next occurrence", reminder.getId());
                    }
                    
                    successCount++;
                    
                } catch (Exception e) {
                    errorCount++;
                    logger.error("Error processing reminder ID: {} for profile: {}: {}", 
                             reminder.getId(), reminder.getfarmerId(), e.getMessage(), e);
                }
            }
            
            logger.info("Reminder processing completed. Success: {}, Errors: {}, Total: {}", 
                    successCount, errorCount, dueReminders.size());
            
        } catch (Exception e) {
            logger.error("Critical error during reminder processing cycle: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Update recurring reminder with next occurrence time
     */
    private void updateRecurringReminder(Reminder reminder) {
        LocalDateTime nextRemindAt = switch (reminder.getRecurrencePattern()) {
            case "DAILY" -> reminder.getRemindAt().plusDays(1);
            case "WEEKLY" -> reminder.getRemindAt().plusWeeks(1);
            case "MONTHLY" -> reminder.getRemindAt().plusMonths(1);
            default -> reminder.getRemindAt().plusDays(1); // Default to daily
        };
        
        reminder.updateRemindTime(nextRemindAt);
        reminderRepository.save(reminder);
    }
}
