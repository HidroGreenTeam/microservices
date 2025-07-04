package com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.valueobjects.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    
    List<Notification> findByProfileIdOrderByCreatedAtDesc(Long profileId);
    
    
    List<Notification> findAllByOrderByCreatedAtDesc();
    
    
    List<Notification> findByNotificationStatus(NotificationStatus notificationStatus);
    
    
    List<Notification> findByScheduledAtBeforeAndNotificationStatus(LocalDateTime scheduledAt, NotificationStatus notificationStatus);
    
    
    List<Notification> findByActivityId(Long activityId);
    
    
    List<Notification> findByCropId(Long cropId);
}
