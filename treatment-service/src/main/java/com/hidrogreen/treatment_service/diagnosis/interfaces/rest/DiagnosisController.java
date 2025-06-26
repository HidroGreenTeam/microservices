package com.hidrogreen.treatment_service.diagnosis.interfaces.rest;

import com.hidrogreen.treatment_service.diagnosis.application.internal.commandservices.DiagnosisCommandServiceImpl;
import com.hidrogreen.treatment_service.diagnosis.application.internal.services.ExternalValidationService;
import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.commands.CreateDiagnosisCommand;
import com.hidrogreen.treatment_service.shared.domain.exceptions.ResourceNotFoundException;
import com.hidrogreen.treatment_service.shared.domain.exceptions.ValidationException;
import com.hidrogreen.treatment_service.shared.interfaces.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/diagnoses")
@Tag(name = "Diagnosis", description = "Diagnosis Storage API - Receives ML results from Detection Service")
public class DiagnosisController {

    private final DiagnosisCommandServiceImpl diagnosisCommandService;
    private final ExternalValidationService externalValidationService;

    public DiagnosisController(DiagnosisCommandServiceImpl diagnosisCommandService,
                             ExternalValidationService externalValidationService) {
        this.diagnosisCommandService = diagnosisCommandService;
        this.externalValidationService = externalValidationService;
    }

    // ============================================
    // CORE ML OPERATIONS
    // ============================================

    @PostMapping("/save-diagnosis")
    @Operation(summary = "Save ML results from Detection Service")
    public ResponseEntity<ApiResponse<Diagnosis>> saveMlResult(@Valid @RequestBody SaveMlResultRequest request) {
        // Complete validation of external dependencies
        externalValidationService.validateExternalResources(request.cropId(), request.farmerId());
        
        var command = new CreateDiagnosisCommand(
                request.cropId(),
                request.farmerId(), 
                request.predictedClass(),
                request.confidence(),
                request.diseaseDetected(),
                request.requiresTreatment(),
                request.imageFilename(),
                request.imageUrl(),
                request.detectionNotes(),
                LocalDateTime.now()
        );

        var diagnosis = diagnosisCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(diagnosis, "Diagnosis saved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update diagnosis data")
    public ResponseEntity<?> updateDiagnosis(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateDiagnosisRequest request) {
        
        try {
            var command = new CreateDiagnosisCommand(
                    null, // cropId no se actualiza
                    null, // farmerId no se actualiza
                    request.predictedClass(),
                    request.confidence(),
                    request.diseaseDetected(),
                    request.requiresTreatment(),
                    null, // filename no se actualiza
                    null, // URL no se actualiza
                    request.detectionNotes(),
                    null  // fecha no se actualiza
            );

            var updatedDiagnosis = diagnosisCommandService.handleUpdate(id, command);
            return ResponseEntity.ok(updatedDiagnosis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Update failed", e.getMessage()));
        }
    }

    // ============================================
    // QUERY OPERATIONS
    // ============================================

    @GetMapping("/{id}")
    @Operation(summary = "Get diagnosis by ID")
    public ResponseEntity<?> getDiagnosisById(@PathVariable Long id) {
        return diagnosisCommandService.handle(id)
                .map(diagnosis -> ResponseEntity.ok(diagnosis))
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis", id));
    }

    @GetMapping("/crops/{cropId}")
    @Operation(summary = "Get all diagnoses for a crop")
    public ResponseEntity<?> getDiagnosesByCropId(@PathVariable Long cropId) {
        try {
            externalValidationService.validateCropExists(cropId);
            
            var diagnoses = diagnosisCommandService.handleGetByCropId(cropId);
            
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", e.getMessage()));
        }
    }

    @GetMapping("/crops/{cropId}/diseased")
    @Operation(summary = "Get diseased diagnoses for a crop")
    public ResponseEntity<?> getDiseasedDiagnosesByCropId(@PathVariable Long cropId) {
        try {
            externalValidationService.validateCropExists(cropId);
            
            var diagnoses = diagnosisCommandService.handleGetDiseasedByCropId(cropId);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", e.getMessage()));
        }
    }

    @GetMapping("/crops/{cropId}/by-disease/{diseaseType}")
    @Operation(summary = "Get diagnoses by specific disease type (optimized)")
    public ResponseEntity<?> getDiagnosesByDiseaseType(
            @PathVariable Long cropId,
            @PathVariable String diseaseType
    ) { 
        if (!isValidDiseaseType(diseaseType)) {
            throw new ValidationException("diseaseType", diseaseType, "Must be one of: miner, nodisease, phoma, redspider, rust");
        }
        
        try {
            externalValidationService.validateCropExists(cropId);
            
            // FIXED: Use optimized repository query instead of stream filter
            var diagnoses = diagnosisCommandService.handleGetByDiseaseTypeAndCrop(cropId, diseaseType);
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", e.getMessage()));
        }
    }

    @GetMapping("/requiring-treatment")
    @Operation(summary = "Get all diagnoses requiring treatment")
    public ResponseEntity<?> getDiagnosesRequiringTreatment() {
        var diagnoses = diagnosisCommandService.handleGetRequiringTreatment();
        
        return ResponseEntity.ok(diagnoses);
    }

    @GetMapping("/crops/{cropId}/latest")
    @Operation(summary = "Get latest diagnosis for a crop")
    public ResponseEntity<?> getLatestDiagnosisByCropId(@PathVariable Long cropId) {
        try {
            externalValidationService.validateCropExists(cropId);
            
            return diagnosisCommandService.handleGetLatestByCropId(cropId)
                    .map(diagnosis -> ResponseEntity.ok(diagnosis))
                    .orElseThrow(() -> new ResourceNotFoundException("Latest diagnosis for crop", cropId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", e.getMessage()));
        }
    }

    @GetMapping("/crops/{cropId}/disease-count")
    @Operation(summary = "Count diseased diagnoses for a crop")
    public ResponseEntity<?> getDiseaseDiagnosisMetrics(@PathVariable Long cropId) {
        try {
            externalValidationService.validateCropExists(cropId);
            
            var diseasedCount = diagnosisCommandService.handleCountDiseasedByCropId(cropId);
            var totalDiagnoses = diagnosisCommandService.handleGetByCropId(cropId).size();
            var hasDisease = diseasedCount > 0;
            
            var metrics = new DiagnosisMetrics(
                    cropId,
                    totalDiagnoses,
                    diseasedCount,
                    hasDisease,
                    totalDiagnoses > 0 ? (double) diseasedCount / totalDiagnoses * 100 : 0.0
            );
            
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", e.getMessage()));
        }
    }

    @GetMapping("/crops/{cropId}/history")
    @Operation(summary = "Get diagnosis history for dashboard")
    public ResponseEntity<?> getDiagnosisHistory(@PathVariable Long cropId) {
        try {
            externalValidationService.validateCropExists(cropId);
            
            var diagnoses = diagnosisCommandService.handleGetByCropId(cropId);
            var diseasedCount = diagnosisCommandService.handleCountDiseasedByCropId(cropId);
            var latest = diagnosisCommandService.handleGetLatestByCropId(cropId).orElse(null);
            
            // Disease analysis
            var diseaseAnalysis = diagnoses.stream()
                    .filter(d -> d.getDiseaseDetected())
                    .collect(java.util.stream.Collectors.groupingBy(
                        Diagnosis::getPredictedClass,
                        java.util.stream.Collectors.counting()
                    ));
            
            var response = new DiagnosisHistoryResponse(
                    diagnoses,
                    diseasedCount,
                    latest,
                    diagnoses.size(),
                    diseaseAnalysis
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Bad request", e.getMessage()));
        }
    }

    // ============================================
    // METADATA & UTILITY ENDPOINTS
    // ============================================

    @GetMapping("/disease-types")
    @Operation(summary = "Get available disease types from ML model")
    public ResponseEntity<List<String>> getAvailableDiseaseTypes() {
        var diseaseTypes = List.of("miner", "nodisease", "phoma", "redspider", "rust");
        return ResponseEntity.ok(diseaseTypes);
    }

    // ============================================
    // SOFT DELETE (RECOMMENDED)
    // ============================================
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete diagnosis (marks as inactive)")
    public ResponseEntity<?> softDeleteDiagnosis(@PathVariable Long id) {
        try {
            // Validate that diagnosis exists first
            var diagnosis = diagnosisCommandService.handle(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Diagnosis", id));
            
            // For now, we'll do hard delete but this should be soft delete
            // TODO: Implement soft delete with 'active' field
            diagnosisCommandService.handleDelete(id);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Deletion failed", e.getMessage()));
        }
    }

    // ============================================
    // VALIDATION & HELPER METHODS
    // ============================================

    private boolean isValidDiseaseType(String diseaseType) {
        return List.of("miner", "nodisease", "phoma", "redspider", "rust")
                .contains(diseaseType);
    }

    // ============================================
    // DTOs
    // ============================================

    public record SaveMlResultRequest(
            Long cropId,
            Long farmerId,
            String predictedClass,      // "miner", "nodisease", "phoma", "redspider", "rust"
            Double confidence,          // 0.0 - 1.0
            Boolean diseaseDetected,    // true si predicted_class != "nodisease"
            Boolean requiresTreatment,  // true si disease_detected && confidence > 0.90
            String imageFilename,
            String imageUrl,
            String detectionNotes
    ) {}

    public record UpdateDiagnosisRequest(
            String predictedClass,
            Double confidence,
            Boolean diseaseDetected,
            Boolean requiresTreatment,
            String detectionNotes
    ) {}

    public record DiagnosisMetrics(
            Long cropId,
            Integer totalDiagnoses,
            Long diseasedCount,
            Boolean hasDisease,
            Double diseasePercentage
    ) {}

    public record DiagnosisHistoryResponse(
            List<Diagnosis> diagnoses,
            Long diseasedCount,
            Diagnosis latestDiagnosis,
            Integer totalCount,
            java.util.Map<String, Long> diseaseAnalysis
    ) {}
}
