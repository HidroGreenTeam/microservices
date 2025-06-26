package com.hidrogreen.treatment_service.diagnosis.application.internal.commandservices;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.commands.CreateDiagnosisCommand;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.persistence.jpa.repositories.DiagnosisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiagnosisCommandServiceImpl {

    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisCommandServiceImpl(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }

    public Diagnosis handle(CreateDiagnosisCommand command) {
        var diagnosis = new Diagnosis(command);
        return diagnosisRepository.save(diagnosis);
    }

    public Optional<Diagnosis> handle(Long id) {
        return diagnosisRepository.findById(id);
    }

    public List<Diagnosis> handleGetByCropId(Long cropId) {
        return diagnosisRepository.findByCropIdOrderByDetectionDateDesc(cropId);
    }

    public List<Diagnosis> handleGetDiseasedByCropId(Long cropId) {
        return diagnosisRepository.findByCropIdAndDiseaseDetected(cropId, true);
    }

    public List<Diagnosis> handleGetRequiringTreatment() {
        return diagnosisRepository.findByRequiresTreatmentAndDiseaseDetected(true, true);
    }

    public Optional<Diagnosis> handleGetLatestByCropId(Long cropId) {
        return diagnosisRepository.findTopByCropIdOrderByDetectionDateDesc(cropId);
    }

    public Long handleCountDiseasedByCropId(Long cropId) {
        return diagnosisRepository.countByCropIdAndDiseaseDetected(cropId, true);
    }

    public boolean handleExistsDiseaseByCropId(Long cropId) {
        return diagnosisRepository.existsByCropIdAndDiseaseDetected(cropId, true);
    }

    public void handleDelete(Long id) {
        diagnosisRepository.deleteById(id);
    }

    public Diagnosis handleUpdate(Long id, CreateDiagnosisCommand command) {
        return diagnosisRepository.findById(id)
                .map(diagnosis -> {
                    diagnosis.update(
                            command.predictedClass(),
                            command.confidence(),
                            command.diseaseDetected(),
                            command.requiresTreatment(),
                            command.detectionNotes()
                    );
                    return diagnosisRepository.save(diagnosis);
                })
                .orElseThrow(() -> new RuntimeException("Diagnosis not found with id: " + id));
    }

    public List<Diagnosis> handleGetByDiseaseType(String predictedClass) {
        return diagnosisRepository.findByPredictedClass(predictedClass);
    }

    public List<Diagnosis> handleGetByDiseaseTypeAndCrop(Long cropId, String predictedClass) {
        return diagnosisRepository.findByCropIdAndPredictedClass(cropId, predictedClass);
    }
}
