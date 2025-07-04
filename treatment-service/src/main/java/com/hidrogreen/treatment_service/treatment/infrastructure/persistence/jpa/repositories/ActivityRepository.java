package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    
    List<Activity> findByCropId(Long cropId);

    
    List<Activity> findByStatus(ActivityStatus status);    
    List<Activity> findByCropIdAndStatus(Long cropId, ActivityStatus status);

    
    @Query("SELECT a FROM Activity a WHERE a.scheduledAt BETWEEN :startOfDay AND :endOfDay")
    List<Activity> findByScheduledAtBetween(@Param("startOfDay") LocalDateTime startOfDay, 
                                          @Param("endOfDay") LocalDateTime endOfDay);

    
    @Query("SELECT a FROM Activity a WHERE a.dueDate < :currentTime AND a.status.status = 'PENDING'")
    List<Activity> findOverdueActivities(@Param("currentTime") LocalDateTime currentTime);

    
    @Query("SELECT a FROM Activity a WHERE a.scheduledAt BETWEEN :fromTime AND :toTime AND a.status.status = 'PENDING'")
    List<Activity> findActivitiesDueSoon(@Param("fromTime") LocalDateTime fromTime, 
                                       @Param("toTime") LocalDateTime toTime);

    
    List<Activity> findByPriorityOrderByScheduledAtAsc(int priority);    
    long countByCropIdAndStatus(Long cropId, ActivityStatus status);
}
