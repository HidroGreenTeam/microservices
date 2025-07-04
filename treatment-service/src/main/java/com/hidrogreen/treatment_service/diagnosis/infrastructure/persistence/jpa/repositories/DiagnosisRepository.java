package com.hidrogreen.treatment_service.diagnosis.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    
    List<Diagnosis> findByCropId(Long cropId);

    
    @Query("SELECT d FROM Diagnosis d WHERE d.cropId IN :cropIds")
    List<Diagnosis> findByCropIds(@Param("cropIds") List<Long> cropIds);

    
    List<Diagnosis> findByStatus(Diagnosis.DiagnosisStatus status);

    
    @Query("SELECT d FROM Diagnosis d WHERE d.status IN ('PENDING', 'PROCESSING')")
    List<Diagnosis> findPendingDiagnosis();

    
    @Query("SELECT d FROM Diagnosis d WHERE d.cropId = :cropId ORDER BY d.createdAt DESC LIMIT 1")
    Diagnosis findLatestByCropId(@Param("cropId") Long cropId);

    
    @Query("SELECT d FROM Diagnosis d WHERE d.cropId IN :cropIds AND d.detectionResult.diseaseDetected = true")
    List<Diagnosis> findByCropIdsWithDiseaseDetected(@Param("cropIds") List<Long> cropIds);
} 