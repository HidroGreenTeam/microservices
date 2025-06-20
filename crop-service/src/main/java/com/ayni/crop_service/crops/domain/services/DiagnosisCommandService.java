package com.ayni.crop_service.crops.domain.services;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.crops.domain.model.commands.StartDiagnosisCommand;
import com.ayni.crop_service.crops.domain.model.commands.CompleteDiagnosisCommand;

import java.util.Optional;

/**
 * Diagnosis command service interface
 */
public interface DiagnosisCommandService {

    /**
     * Start a new diagnosis
     *
     * @param command the start diagnosis command
     * @return the created diagnosis ID
     */
    Long handle(StartDiagnosisCommand command);

    /**
     * Complete a diagnosis
     *
     * @param command the complete diagnosis command
     * @return the completed diagnosis
     */
    Optional<Diagnosis> handle(CompleteDiagnosisCommand command);

    /**
     * Mark diagnosis as failed
     *
     * @param diagnosisId the diagnosis ID
     * @param reason the failure reason
     * @return the updated diagnosis
     */
    Optional<Diagnosis> markAsFailed(Long diagnosisId, String reason);
}
