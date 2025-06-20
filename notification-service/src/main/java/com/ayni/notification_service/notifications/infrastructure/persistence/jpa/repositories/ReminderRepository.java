package com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReminderRepository
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    /**
     * Find active reminders by profile ID
     * @param profileId the profile ID
     * @param isActive the active status
     * @return a list of reminders
     */
    List<Reminder> findByProfileIdAndIsActive(Long profileId, boolean isActive);
    
    /**
     * Find due reminders that are active
     * @param remindAt the remind time
     * @param isActive the active status
     * @return a list of reminders
     */
    List<Reminder> findByRemindAtBeforeAndIsActive(LocalDateTime remindAt, boolean isActive);
    
    /**
     * Find active reminders by activity ID
     * @param activityId the activity ID
     * @param isActive the active status
     * @return a list of reminders
     */
    List<Reminder> findByActivityIdAndIsActive(Long activityId, boolean isActive);
    
    /**
     * Find active reminders by crop ID
     * @param cropId the crop ID
     * @param isActive the active status
     * @return a list of reminders
     */
    List<Reminder> findByCropIdAndIsActive(Long cropId, boolean isActive);
    
    /**
     * Find recurring reminders that are active
     * @param isRecurring the recurring status
     * @param isActive the active status
     * @return a list of reminders
     */
    List<Reminder> findByIsRecurringAndIsActive(boolean isRecurring, boolean isActive);
}
