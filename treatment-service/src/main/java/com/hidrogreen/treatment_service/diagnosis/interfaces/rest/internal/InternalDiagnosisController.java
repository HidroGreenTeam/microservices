package com.hidrogreen.treatment_service.diagnosis.interfaces.rest.internal;

import com.hidrogreen.treatment_service.diagnosis.application.internal.commandservices.DiagnosisCommandServiceImpl;
import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.shared.interfaces.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/api/v1/diagnoses")
@Tag(name = "Internal Diagnosis", description = "Internal endpoints for service-to-service communication")
@PreAuthorize("hasRole('SERVICE')")
@Hidden
public class InternalDiagnosisController {

    private final DiagnosisCommandServiceImpl diagnosisCommandService;

    public InternalDiagnosisController(DiagnosisCommandServiceImpl diagnosisCommandService) {
        this.diagnosisCommandService = diagnosisCommandService;
    }

    @GetMapping("/crops/{cropId}/exists")
    @Operation(summary = "Check if crop has any diagnosis (Internal)")
    public ResponseEntity<Boolean> existsDiagnosisByCropId(@PathVariable Long cropId) {
        var diagnoses = diagnosisCommandService.handleGetByCropId(cropId);
        boolean exists = !diagnoses.isEmpty();
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/crops/{cropId}/count")
    @Operation(summary = "Count total diagnoses for a crop (Internal)")
    public ResponseEntity<Long> countDiagnosesByCropId(@PathVariable Long cropId) {
        var diagnoses = diagnosisCommandService.handleGetByCropId(cropId);
        long count = diagnoses.size();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/crops/{cropId}/basic")
    @Operation(summary = "Get basic diagnosis info for a crop (Internal)")
    public ResponseEntity<List<BasicDiagnosisInfo>> getBasicDiagnosisInfo(@PathVariable Long cropId) {
        var diagnoses = diagnosisCommandService.handleGetByCropId(cropId);
        var basicInfo = diagnoses.stream()
                .map(d -> new BasicDiagnosisInfo(
                        d.getId(),
                        d.getCropId(),
                        d.getPredictedClass(),
                        d.getDiseaseDetected(),
                        d.getRequiresTreatment()
                ))
                .toList();
        
        return ResponseEntity.ok(basicInfo);
    }

    public record BasicDiagnosisInfo(
            Long id,
            Long cropId,
            String predictedClass,
            Boolean diseaseDetected,
            Boolean requiresTreatment
    ) {}
} 