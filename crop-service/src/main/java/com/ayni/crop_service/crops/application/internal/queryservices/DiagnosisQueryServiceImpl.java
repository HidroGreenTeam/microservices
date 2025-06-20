package com.ayni.crop_service.crops.application.internal.queryservices;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.crops.domain.model.queries.GetDiagnosesByCropIdQuery;
import com.ayni.crop_service.crops.domain.services.DiagnosisQueryService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.DiagnosisRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Diagnosis query service implementation
 */
@Service
public class DiagnosisQueryServiceImpl implements DiagnosisQueryService {

    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisQueryServiceImpl(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }

    @Override
    public Optional<Diagnosis> getDiagnosisById(Long diagnosisId) {
        return diagnosisRepository.findById(diagnosisId);
    }

    @Override
    public List<Diagnosis> handle(GetDiagnosesByCropIdQuery query) {
        return diagnosisRepository.findByCropId(query.getCropId());
    }

    @Override
    public List<Diagnosis> getDiagnosesByProfileId(Long profileId) {
        return diagnosisRepository.findByProfileId(profileId);
    }

    @Override
    public List<Diagnosis> getPendingDiagnoses() {
        return diagnosisRepository.findPendingDiagnoses();
    }
}
