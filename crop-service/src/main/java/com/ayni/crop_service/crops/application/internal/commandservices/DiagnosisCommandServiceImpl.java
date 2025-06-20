package com.ayni.crop_service.crops.application.internal.commandservices;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.crops.domain.model.commands.StartDiagnosisCommand;
import com.ayni.crop_service.crops.domain.model.commands.CompleteDiagnosisCommand;
import com.ayni.crop_service.crops.domain.services.DiagnosisCommandService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.DiagnosisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Diagnosis command service implementation
 */
@Service
public class DiagnosisCommandServiceImpl implements DiagnosisCommandService {

    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisCommandServiceImpl(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }

    @Override
    @Transactional
    public Long handle(StartDiagnosisCommand command) {
        Diagnosis diagnosis = new Diagnosis(
            command.getCropId(),
            command.getProfileId(),
            command.imageUrl()
        );

        Diagnosis savedDiagnosis = diagnosisRepository.save(diagnosis);
        return savedDiagnosis.getId();
    }

    @Override
    @Transactional
    public Optional<Diagnosis> handle(CompleteDiagnosisCommand command) {
        return diagnosisRepository.findById(command.diagnosisId())
            .map(diagnosis -> {
                diagnosis.completeWithResult(
                    command.getDetectionResult(),
                    command.confidenceScore()
                );
                return diagnosisRepository.save(diagnosis);
            });
    }

    @Override
    @Transactional
    public Optional<Diagnosis> markAsFailed(Long diagnosisId, String reason) {
        return diagnosisRepository.findById(diagnosisId)
            .map(diagnosis -> {
                diagnosis.markAsFailed(reason);
                return diagnosisRepository.save(diagnosis);
            });
    }
}
