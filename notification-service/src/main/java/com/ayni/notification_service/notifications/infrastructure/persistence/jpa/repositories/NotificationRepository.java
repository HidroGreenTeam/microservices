package com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

 
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByProfileId(Long profileId);
    List<Notification> findByProfileIdOrderByCreatedAtDesc(Long profileId);
    
}
