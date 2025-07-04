package com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    
    List<Reminder> findByProfileIdAndIsActive(Long profileId, boolean isActive);
    
    
    List<Reminder> findByRemindAtBeforeAndIsActive(LocalDateTime remindAt, boolean isActive);
    
    
    List<Reminder> findByActivityIdAndIsActive(Long activityId, boolean isActive);
    
    
    List<Reminder> findByCropIdAndIsActive(Long cropId, boolean isActive);
    
    
    List<Reminder> findByIsRecurringAndIsActive(boolean isRecurring, boolean isActive);
}
