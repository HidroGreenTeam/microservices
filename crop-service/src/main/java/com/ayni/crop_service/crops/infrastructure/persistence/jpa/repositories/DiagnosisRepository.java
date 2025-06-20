package com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Diagnosis repository
 */
@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    /**
     * Find diagnoses by crop ID
     *
     * @param cropId the crop ID
     * @return the list of diagnoses
     */
    List<Diagnosis> findByCropId(Long cropId);

    /**
     * Find diagnoses by profile ID
     *
     * @param profileId the profile ID
     * @return the list of diagnoses
     */
    List<Diagnosis> findByProfileId(Long profileId);

    /**
     * Find diagnoses by status
     *
     * @param status the diagnosis status
     * @return the list of diagnoses
     */
    List<Diagnosis> findByStatus(Diagnosis.DiagnosisStatus status);

    /**
     * Find pending diagnoses
     *
     * @return the list of pending diagnoses
     */
    @Query("SELECT d FROM Diagnosis d WHERE d.status IN ('PENDING', 'PROCESSING')")
    List<Diagnosis> findPendingDiagnoses();

    /**
     * Find latest diagnosis by crop ID
     *
     * @param cropId the crop ID
     * @return the latest diagnosis
     */
    @Query("SELECT d FROM Diagnosis d WHERE d.cropId = :cropId ORDER BY d.createdAt DESC LIMIT 1")
    Diagnosis findLatestByCropId(@Param("cropId") Long cropId);

    /**
     * Find diagnoses with disease detected
     *
     * @param profileId the profile ID
     * @return the list of diagnoses with disease detected
     */
    @Query("SELECT d FROM Diagnosis d WHERE d.profileId = :profileId AND d.detectionResult.diseaseDetected = true")
    List<Diagnosis> findByProfileIdWithDiseaseDetected(@Param("profileId") Long profileId);
}
