package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standalone activity repository 🆓
 */
@Repository
public interface StandaloneActivityRepository extends JpaRepository<StandaloneActivity, Long> {

    /**
     * Find standalone activities by crop ID
     *
     * @param cropId the crop ID
     * @return the list of standalone activities
     */
    List<StandaloneActivity> findByCropId(Long cropId);

    /**
     * Find standalone activities by created by user
     *
     * @param createdByUser the user who created the activity
     * @return the list of standalone activities
     */
    List<StandaloneActivity> findByCreatedByUser(String createdByUser);    /**
     * Find standalone activities by crop ID and user
     *
     * @param cropId the crop ID
     * @param createdByUser the user who created the activity
     * @return the list of standalone activities
     */
    List<StandaloneActivity> findByCropIdAndCreatedByUser(Long cropId, String createdByUser);

    /**
     * Find standalone activities with reminders enabled
     *
     * @return the list of activities with reminders
     */
    List<StandaloneActivity> findByReminderEnabledTrue();

    /**
     * Find standalone activities needing reminders
     *
     * @param fromTime the from time
     * @param toTime the to time
     * @return the list of activities needing reminders
     */
    @Query("SELECT sa FROM StandaloneActivity sa WHERE sa.reminderEnabled = true " +
           "AND sa.scheduledAt BETWEEN :fromTime AND :toTime " +
           "AND sa.status.status = 'PENDING'")
    List<StandaloneActivity> findActivitiesNeedingReminders(@Param("fromTime") LocalDateTime fromTime,
                                                           @Param("toTime") LocalDateTime toTime);

    /**
     * Find standalone activities by frequency
     *
     * @param frequency the frequency
     * @return the list of activities
     */
    @Query("SELECT sa FROM StandaloneActivity sa WHERE sa.frequency.frequency = :frequency")
    List<StandaloneActivity> findByFrequency(@Param("frequency") String frequency);
}
