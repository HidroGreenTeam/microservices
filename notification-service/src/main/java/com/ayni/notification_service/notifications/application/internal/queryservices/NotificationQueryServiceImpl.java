package com.ayni.notification_service.notifications.application.internal.queryservices;

import com.ayni.notification_service.notifications.domain.model.aggregates.Notification;
import com.ayni.notification_service.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.ayni.notification_service.notifications.domain.model.queries.GetNotificationsByProfileIdQuery;
import com.ayni.notification_service.notifications.domain.services.NotificationQueryService;
import com.ayni.notification_service.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationQueryServiceImpl.class);
    
    private final NotificationRepository notificationRepository;
    
    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public List<Notification> handle(GetNotificationsByProfileIdQuery query) {
        logger.debug("Handling GetNotificationsByProfileIdQuery for profileId: {}", query.profileId());
        
        try {
            List<Notification> notifications = notificationRepository.findByProfileIdOrderByCreatedAtDesc(query.profileId());
            logger.info("Retrieved {} notifications for profileId: {}", notifications.size(), query.profileId());
            return notifications;
            
        } catch (Exception e) {
            logger.error("Error retrieving notifications for profileId: {}: {}", query.profileId(), e.getMessage(), e);
            throw e;
        }    }
    
    @Override
    public List<Notification> handle(GetAllNotificationsQuery query) {
        logger.debug("Handling GetAllNotificationsQuery");
        
        try {
            List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
            logger.info("Retrieved {} total notifications", notifications.size());
            return notifications;
            
        } catch (Exception e) {
            logger.error("Error retrieving all notifications: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        logger.debug("Handling GetNotificationByIdQuery for notificationId: {}", query.notificationId());
        
        try {
            Optional<Notification> notification = notificationRepository.findById(query.notificationId());
            if (notification.isPresent()) {
                logger.info("Retrieved notification with ID: {}", query.notificationId());
            } else {
                logger.warn("Notification with ID: {} not found", query.notificationId());
            }
            return notification;
            
        } catch (Exception e) {
            logger.error("Error retrieving notification with ID: {}: {}", query.notificationId(), e.getMessage(), e);
            throw e;
        }
    }
}
