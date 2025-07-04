package com.hidrogreen.treatment_service.diagnosis.application.internal.queryservices;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.queries.GetDiagnosisByCropIdQuery;
import com.hidrogreen.treatment_service.diagnosis.domain.services.DiagnosisQueryService;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.persistence.jpa.repositories.DiagnosisRepository;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.CropServiceClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class DiagnosisQueryServiceImpl implements DiagnosisQueryService {

    private final DiagnosisRepository diagnosisRepository;
    private final CropServiceClient cropServiceClient;

    public DiagnosisQueryServiceImpl(DiagnosisRepository diagnosisRepository, 
                                   CropServiceClient cropServiceClient) {
        this.diagnosisRepository = diagnosisRepository;
        this.cropServiceClient = cropServiceClient;
    }

    @Override
    public Optional<Diagnosis> getDiagnosisById(Long diagnosisId) {
        return diagnosisRepository.findById(diagnosisId);
    }

    @Override
    public List<Diagnosis> handle(GetDiagnosisByCropIdQuery query) {
        return diagnosisRepository.findByCropId(query.getCropId());
    }

    @Override
    public List<Diagnosis> getDiagnosisByProfileId(Long profileId) {
        try {
            
            List<CropServiceClient.CropDTO> crops = cropServiceClient.getCropsByFarmerId(profileId);
            
            if (crops.isEmpty()) {
                return Collections.emptyList();
            }
            
            
            List<Long> cropIds = crops.stream()
                    .map(CropServiceClient.CropDTO::id)
                    .toList();
            
            
            return diagnosisRepository.findByCropIds(cropIds);
        } catch (Exception e) {
            
            return Collections.emptyList();
        }
    }

    @Override
    public List<Diagnosis> getPendingDiagnosis() {
        return diagnosisRepository.findPendingDiagnosis();
    }
} 