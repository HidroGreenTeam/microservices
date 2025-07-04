package com.ayni.notification_service.notifications.application.internal.queryservices;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.domain.model.queries.GetPendingRemindersQuery;
import com.ayni.notification_service.notifications.domain.services.ReminderQueryService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.ReminderRepository;
import com.ayni.notification_service.notifications.application.internal.outboundservices.acl.ExternalProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReminderQueryServiceImpl implements ReminderQueryService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReminderQueryServiceImpl.class);
    
    private final ReminderRepository reminderRepository;
    private final ExternalProfileService externalProfileService;
    
    public ReminderQueryServiceImpl(ReminderRepository reminderRepository, ExternalProfileService externalProfileService) {
        this.reminderRepository = reminderRepository;
        this.externalProfileService = externalProfileService;
    }
    
    @Override
    public List<Reminder> handle(GetPendingRemindersQuery query) {
        logger.debug("Handling GetPendingRemindersQuery");
          try {
            List<Reminder> reminders = reminderRepository.findByRemindAtBeforeAndIsActive(LocalDateTime.now(), true);
            logger.info("Retrieved {} pending reminders", reminders.size());
            return reminders;
            
        } catch (Exception e) {
            logger.error("Error retrieving pending reminders: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public List<Reminder> getRemindersByProfileId(Long profileId) {
        logger.debug("Retrieving reminders for profileId: {}", profileId);
        
        
        if (!externalProfileService.existsProfile(profileId)) {
            logger.warn("Profile not found with id: {}", profileId);
            throw new IllegalArgumentException("Profile not found with id: " + profileId);
        }
        
        try {
            List<Reminder> reminders = reminderRepository.findByProfileIdAndIsActive(profileId, true);
            logger.info("Retrieved {} active reminders for profileId: {}", reminders.size(), profileId);
            return reminders;
            
        } catch (Exception e) {
            logger.error("Error retrieving reminders for profileId: {}: {}", profileId, e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public List<Reminder> getRecurringReminders() {
        logger.debug("Retrieving recurring reminders");
          try {
            List<Reminder> reminders = reminderRepository.findByIsRecurringAndIsActive(true, true);
            logger.info("Retrieved {} recurring reminders", reminders.size());
            return reminders;
            
        } catch (Exception e) {
            logger.error("Error retrieving recurring reminders: {}", e.getMessage(), e);
            throw e;
        }
    }
}
