package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.StandaloneActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StandaloneActivityRepository extends JpaRepository<StandaloneActivity, Long> {

    
    List<StandaloneActivity> findByCropId(Long cropId);

    
    List<StandaloneActivity> findByCreatedByUser(String createdByUser);    
    List<StandaloneActivity> findByCropIdAndCreatedByUser(Long cropId, String createdByUser);

    
    List<StandaloneActivity> findByReminderEnabledTrue();

    
    @Query("SELECT sa FROM StandaloneActivity sa WHERE sa.reminderEnabled = true " +
           "AND sa.scheduledAt BETWEEN :fromTime AND :toTime " +
           "AND sa.status.status = 'PENDING'")
    List<StandaloneActivity> findActivitiesNeedingReminders(@Param("fromTime") LocalDateTime fromTime,
                                                           @Param("toTime") LocalDateTime toTime);

    
    @Query("SELECT sa FROM StandaloneActivity sa WHERE sa.frequency.frequency = :frequency")
    List<StandaloneActivity> findByFrequency(@Param("frequency") String frequency);
}
