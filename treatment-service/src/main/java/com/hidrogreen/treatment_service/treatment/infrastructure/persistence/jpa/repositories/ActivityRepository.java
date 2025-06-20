package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Activity;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity repository
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Find activities by crop ID
     *
     * @param cropId the crop ID
     * @return the list of activities
     */
    List<Activity> findByCropId(Long cropId);

    /**
     * Find activities by status
     *
     * @param status the activity status
     * @return the list of activities
     */
    List<Activity> findByStatus(ActivityStatus status);    /**
     * Find activities by crop ID and status
     *
     * @param cropId the crop ID
     * @param status the activity status
     * @return the list of activities
     */
    List<Activity> findByCropIdAndStatus(Long cropId, ActivityStatus status);

    /**
     * Find activities scheduled for a specific date
     *
     * @param startOfDay the start of day
     * @param endOfDay the end of day
     * @return the list of activities
     */
    @Query("SELECT a FROM Activity a WHERE a.scheduledAt BETWEEN :startOfDay AND :endOfDay")
    List<Activity> findByScheduledAtBetween(@Param("startOfDay") LocalDateTime startOfDay, 
                                          @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Find overdue activities
     *
     * @param currentTime the current time
     * @return the list of overdue activities
     */
    @Query("SELECT a FROM Activity a WHERE a.dueDate < :currentTime AND a.status.status = 'PENDING'")
    List<Activity> findOverdueActivities(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find activities due soon (for reminders)
     *
     * @param fromTime the from time
     * @param toTime the to time
     * @return the list of activities due soon
     */
    @Query("SELECT a FROM Activity a WHERE a.scheduledAt BETWEEN :fromTime AND :toTime AND a.status.status = 'PENDING'")
    List<Activity> findActivitiesDueSoon(@Param("fromTime") LocalDateTime fromTime, 
                                       @Param("toTime") LocalDateTime toTime);

    /**
     * Find activities by priority
     *
     * @param priority the priority
     * @return the list of activities
     */
    List<Activity> findByPriorityOrderByScheduledAtAsc(int priority);    /**
     * Count activities by crop ID and status
     *
     * @param cropId the crop ID
     * @param status the activity status
     * @return the count
     */
    long countByCropIdAndStatus(Long cropId, ActivityStatus status);
}
