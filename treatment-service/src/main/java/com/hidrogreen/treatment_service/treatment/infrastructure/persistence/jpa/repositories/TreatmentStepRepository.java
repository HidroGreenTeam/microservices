package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentStepStatus;

@Repository
public interface TreatmentStepRepository extends JpaRepository<TreatmentStep, Long> {
    
    List<TreatmentStep> findByScheduledDateBeforeAndStatus(LocalDateTime now, TreatmentStepStatus status);
    List<TreatmentStep> findByHasReminderTrue();
    List<TreatmentStep> findByHasReminderTrueAndStatusNot(TreatmentStepStatus status);
    List<TreatmentStep> findByHasReminderTrueAndStatusNotAndScheduledDateLessThanEqual(
        TreatmentStepStatus status, 
        LocalDateTime reminderTime
    );
    
    @Query("SELECT ts FROM TreatmentStep ts JOIN FETCH ts.treatment WHERE ts.hasReminder = true")
    List<TreatmentStep> findByHasReminderTrueWithTreatment();
}
