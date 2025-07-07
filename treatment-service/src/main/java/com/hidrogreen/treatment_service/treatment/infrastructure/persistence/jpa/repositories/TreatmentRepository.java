package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    
    List<Treatment> findByCropId(Long cropId);
    
    List<Treatment> findByProfileId(Long profileId);
    
    Optional<Treatment> findByStepsId(Long stepId);
    
    List<Treatment> findByEndDateBeforeAndStatusStatusNotIn(LocalDateTime now, List<String> statuses);
    
    List<TreatmentStep> findByStepsScheduledDateBeforeAndStepsStatusStatus(LocalDateTime now, String status);
    
    List<TreatmentStep> findByStepsHasReminderTrue();
    
    List<TreatmentStep> findByStepsHasReminderTrueAndStepsStatusStatusNot(String status);
    
    boolean existsByDiagnosisId(Long diagnosisId);
}
