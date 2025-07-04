package com.hidrogreen.treatment_service.diagnosis.application.internal.commandservices;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.commands.CompleteDiagnosisCommand;
import com.hidrogreen.treatment_service.diagnosis.domain.services.DiagnosisCommandService;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.persistence.jpa.repositories.DiagnosisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class DiagnosisCommandServiceImpl implements DiagnosisCommandService {

    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisCommandServiceImpl(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
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

    @Override
    public Optional<Diagnosis> getDiagnosisById(Long diagnosisId) {
        return diagnosisRepository.findById(diagnosisId);
    }
} 