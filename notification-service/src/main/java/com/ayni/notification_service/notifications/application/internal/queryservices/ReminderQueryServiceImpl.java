package com.ayni.notification_service.notifications.application.internal.queryservices;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.domain.model.queries.GetPendingRemindersQuery;
import com.ayni.notification_service.notifications.domain.services.ReminderQueryService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReminderQueryServiceImpl
 */
@Service
public class ReminderQueryServiceImpl implements ReminderQueryService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReminderQueryServiceImpl.class);
    
    private final ReminderRepository reminderRepository;
    
    public ReminderQueryServiceImpl(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
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
    public List<Reminder> getRemindersByfarmerId(Long farmerId) {
        logger.debug("Retrieving reminders for farmerId: {}", farmerId);
          try {
            List<Reminder> reminders = reminderRepository.findByfarmerIdAndIsActive(farmerId, true);
            logger.info("Retrieved {} active reminders for farmerId: {}", reminders.size(), farmerId);
            return reminders;
            
        } catch (Exception e) {
            logger.error("Error retrieving reminders for farmerId: {}: {}", farmerId, e.getMessage(), e);
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
