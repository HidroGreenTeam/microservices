package com.ayni.notification_service.notifications.application.internal.commandservices;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.domain.model.commands.ScheduleReminderCommand;
import com.ayni.notification_service.notifications.domain.services.ReminderCommandService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.ReminderRepository;
import com.ayni.notification_service.notifications.application.internal.outboundservices.acl.ExternalProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ReminderCommandServiceImpl implements ReminderCommandService {
    
    private static final Logger log = LoggerFactory.getLogger(ReminderCommandServiceImpl.class);
    
    private final ReminderRepository reminderRepository;
    private final ExternalProfileService externalProfileService;
    
    public ReminderCommandServiceImpl(ReminderRepository reminderRepository, ExternalProfileService externalProfileService) {
        this.reminderRepository = reminderRepository;
        this.externalProfileService = externalProfileService;
    }
    
    @Override
    public Long handle(ScheduleReminderCommand command) {
        log.info("Processing ScheduleReminderCommand for profileId: {}, remindAt: {}", 
                command.profileId(), command.remindAt());
        
        
        if (!externalProfileService.existsProfile(command.profileId())) {
            log.warn("Profile not found with id: {}", command.profileId());
            throw new IllegalArgumentException("Profile not found with id: " + command.profileId());
        }
        
        try {
            Long profileId = command.profileId();
            
            Reminder reminder;
            if (command.activityId() != null) {
                Long activityId = command.activityId();
                reminder = new Reminder(profileId, activityId, command.notificationChannel(), 
                                      command.title(), command.message(), command.remindAt());
                log.debug("Created reminder for activity: {}", activityId);
            } else if (command.cropId() != null) {
                Long cropId = command.cropId();
                reminder = new Reminder(profileId, cropId, command.notificationChannel(), 
                                      command.title(), command.message(), command.remindAt(), true);
                log.debug("Created reminder for crop: {}", cropId);
            } else {
                reminder = new Reminder(profileId, command.notificationChannel(), 
                                      command.title(), command.message(), command.remindAt());
                log.debug("Created general reminder for profile: {}", profileId);
            }
            
            if (command.isRecurring()) {
                reminder.setRecurring(command.recurrencePattern());
                log.debug("Set reminder as recurring with pattern: {}", command.recurrencePattern());            }
            
            Reminder savedReminder = reminderRepository.save(reminder);
            log.info("Reminder scheduled successfully with ID: {}", savedReminder.getId());
            
            
            savedReminder.publishCreatedEvent();
            
            return savedReminder.getId();
            
        } catch (Exception e) {
            log.error("Error handling ScheduleReminderCommand for profileId: {}: {}", 
                     command.profileId(), e.getMessage(), e);
            throw new RuntimeException("Failed to schedule reminder", e);
        }
    }
    
    @Override
    public void cancelReminder(Long reminderId) {
        log.info("Cancelling reminder with ID: {}", reminderId);
        
        try {
            reminderRepository.findById(reminderId).ifPresentOrElse(reminder -> {
                reminder.deactivate();
                reminderRepository.save(reminder);
                log.info("Reminder {} cancelled successfully", reminderId);
            }, () -> {
                log.warn("Reminder with ID {} not found for cancellation", reminderId);
                throw new RuntimeException("Reminder not found: " + reminderId);
            });
            
        } catch (Exception e) {
            log.error("Error cancelling reminder {}: {}", reminderId, e.getMessage(), e);
            throw new RuntimeException("Failed to cancel reminder", e);
        }
    }
    
    @Override
    public void updateReminder(Long reminderId, ScheduleReminderCommand command) {
        log.info("Updating reminder with ID: {}", reminderId);
        
        try {
            reminderRepository.findById(reminderId).ifPresentOrElse(reminder -> {
                reminder.updateRemindTime(command.remindAt());
                reminderRepository.save(reminder);
                log.info("Reminder {} updated successfully to new time: {}", reminderId, command.remindAt());
            }, () -> {
                log.warn("Reminder with ID {} not found for update", reminderId);
                throw new RuntimeException("Reminder not found: " + reminderId);
            });
            
        } catch (Exception e) {
            log.error("Error updating reminder {}: {}", reminderId, e.getMessage(), e);
            throw new RuntimeException("Failed to update reminder", e);
        }
    }
}
