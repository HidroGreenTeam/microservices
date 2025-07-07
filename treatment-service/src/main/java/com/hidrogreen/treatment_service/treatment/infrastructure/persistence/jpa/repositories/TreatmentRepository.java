package com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    
    List<Treatment> findByCropId(Long cropId);
    
    List<Treatment> findByProfileId(Long profileId);
    
    @Query("SELECT t FROM Treatment t JOIN t.steps s WHERE s.id = :stepId")
    Optional<Treatment> findByStepsId(@Param("stepId") Long stepId);
    
    @Query("SELECT t FROM Treatment t WHERE t.endDate < :now AND t.status.status NOT IN :statuses")
    List<Treatment> findByEndDateBeforeAndStatusStatusNotIn(
        @Param("now") LocalDateTime now, 
        @Param("statuses") List<String> statuses
    );
    
    boolean existsByDiagnosisId(Long diagnosisId);
}
