package com.hidrogreen.treatment_service.diagnosis.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    // Métodos estándar de Spring Data JPA (sin @Query)
    List<Diagnosis> findByCropId(Long cropId);

    List<Diagnosis> findByCropIdOrderByDetectionDateDesc(Long cropId);

    List<Diagnosis> findByCropIdAndDiseaseDetected(Long cropId, Boolean diseaseDetected);

    List<Diagnosis> findByRequiresTreatmentAndDiseaseDetected(Boolean requiresTreatment, Boolean diseaseDetected);

    List<Diagnosis> findByDetectionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<Diagnosis> findTopByCropIdOrderByDetectionDateDesc(Long cropId);

    Long countByCropIdAndDiseaseDetected(Long cropId, Boolean diseaseDetected);

    boolean existsByCropIdAndDiseaseDetected(Long cropId, Boolean diseaseDetected);

    // Métodos adicionales simples
    List<Diagnosis> findByDiseaseDetected(Boolean diseaseDetected);

    List<Diagnosis> findByRequiresTreatment(Boolean requiresTreatment);

    List<Diagnosis> findByPredictedClass(String predictedClass);

    List<Diagnosis> findByCropIdAndPredictedClass(Long cropId, String predictedClass);
} 