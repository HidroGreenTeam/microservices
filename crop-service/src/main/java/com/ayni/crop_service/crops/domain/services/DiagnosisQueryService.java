package com.ayni.crop_service.crops.domain.services;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.crops.domain.model.queries.GetDiagnosesByCropIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Diagnosis query service interface
 */
public interface DiagnosisQueryService {

    /**
     * Get diagnosis by ID
     *
     * @param diagnosisId the diagnosis ID
     * @return the diagnosis if found
     */
    Optional<Diagnosis> getDiagnosisById(Long diagnosisId);

    /**
     * Get diagnoses by crop ID
     *
     * @param query the get diagnoses by crop ID query
     * @return the list of diagnoses
     */
    List<Diagnosis> handle(GetDiagnosesByCropIdQuery query);

    /**
     * Get diagnoses by profile ID
     *
     * @param profileId the profile ID
     * @return the list of diagnoses
     */
    List<Diagnosis> getDiagnosesByProfileId(Long profileId);

    /**
     * Get pending diagnoses
     *
     * @return the list of pending diagnoses
     */
    List<Diagnosis> getPendingDiagnoses();
}
