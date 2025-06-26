package com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NotificationRepository
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by user ID ordered by creation date descending
     * @param userId the user ID
     * @return a list of notifications
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find notifications by user ID and role ordered by creation date descending
     * @param userId the user ID
     * @param recipientRole the recipient role
     * @return a list of notifications
     */
    List<Notification> findByUserIdAndRecipientRoleOrderByCreatedAtDesc(Long userId, com.ayni.notification_service.notifications.domain.model.valueobjects.UserRole recipientRole);
    
    /**
     * @deprecated Use findByUserIdOrderByCreatedAtDesc instead
     * Find notifications by profile ID ordered by creation date descending (for compatibility)
     * @param farmerId the profile ID (mapped to userId)
     * @return a list of notifications
     */
    @Deprecated
    default List<Notification> findByfarmerIdOrderByCreatedAtDesc(Long farmerId) {
        return findByUserIdAndRecipientRoleOrderByCreatedAtDesc(farmerId, com.ayni.notification_service.notifications.domain.model.valueobjects.UserRole.FARMER);
    }
    
    /**
     * Find all notifications ordered by creation date descending
     * @return a list of all notifications
     */
    List<Notification> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find notifications by status
     * @param notificationStatus the notification status
     * @return a list of notifications
     */
    List<Notification> findByNotificationStatus(NotificationStatus notificationStatus);
    
    /**
     * Find pending notifications that are due now
     * @param scheduledAt the scheduled time
     * @param notificationStatus the notification status
     * @return a list of notifications
     */
    List<Notification> findByScheduledAtBeforeAndNotificationStatus(LocalDateTime scheduledAt, NotificationStatus notificationStatus);
    
    /**
     * Find notifications by activity ID
     * @param activityId the activity ID
     * @return a list of notifications
     */
    List<Notification> findByActivityId(Long activityId);
    
    /**
     * Find notifications by crop ID
     * @param cropId the crop ID
     * @return a list of notifications
     */
    List<Notification> findByCropId(Long cropId);
}
