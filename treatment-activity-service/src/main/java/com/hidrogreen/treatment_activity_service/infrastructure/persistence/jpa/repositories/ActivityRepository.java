package com.hidrogreen.treatment_activity_service.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_activity_service.domain.model.entities.Activity;
import com.hidrogreen.treatment_activity_service.domain.model.valueobjects.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    List<Activity> findByTreatmentId(Long treatmentId);
    
    List<Activity> findByTreatmentIdAndStatus(Long treatmentId, ActivityStatus status);
    
    @Query("SELECT a FROM Activity a WHERE a.treatment.userId = :userId")
    List<Activity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Activity a WHERE a.treatment.userId = :userId AND a.status = :status")
    List<Activity> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ActivityStatus status);
    
    @Query("SELECT a FROM Activity a WHERE a.notificationSent = false AND a.status = 'PENDING' AND a.scheduledDateTime > :now")
    List<Activity> findPendingActivitiesForNotification(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Activity a WHERE a.scheduledDateTime BETWEEN :startDate AND :endDate")
    List<Activity> findActivitiesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Activity a WHERE a.treatment.userId = :userId AND a.scheduledDateTime BETWEEN :startDate AND :endDate ORDER BY a.scheduledDateTime ASC")
    List<Activity> findUserActivitiesBetweenDates(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
