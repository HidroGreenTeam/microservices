package com.ayni.crop_service.crops.interfaces.rest;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.crops.domain.model.commands.StartDiagnosisCommand;
import com.ayni.crop_service.crops.domain.model.queries.GetDiagnosesByCropIdQuery;
import com.ayni.crop_service.crops.domain.services.DiagnosisCommandService;
import com.ayni.crop_service.crops.domain.services.DiagnosisQueryService;
import com.ayni.crop_service.crops.interfaces.rest.resources.DiagnosisResource;
import com.ayni.crop_service.crops.interfaces.rest.resources.StartDiagnosisResource;
import com.ayni.crop_service.crops.interfaces.rest.transform.DiagnosisResourceFromEntityAssembler;
import com.ayni.crop_service.crops.interfaces.rest.transform.StartDiagnosisCommandFromResourceAssembler;
import com.ayni.crop_service.crops.application.internal.outboundservices.detection.CropDiseaseDetectionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Diagnosis REST controller
 */
@RestController
@RequestMapping(value = "/api/v1/diagnosis", produces = MediaType.APPLICATION_JSON_VALUE)
public class DiagnosisController {

    private final DiagnosisCommandService diagnosisCommandService;
    private final DiagnosisQueryService diagnosisQueryService;
    private final CropDiseaseDetectionService cropDiseaseDetectionService;

    public DiagnosisController(DiagnosisCommandService diagnosisCommandService, 
                             DiagnosisQueryService diagnosisQueryService,
                             CropDiseaseDetectionService cropDiseaseDetectionService) {
        this.diagnosisCommandService = diagnosisCommandService;
        this.diagnosisQueryService = diagnosisQueryService;
        this.cropDiseaseDetectionService = cropDiseaseDetectionService;
    }

    /**
     * Start a new diagnosis
     */
    @PostMapping
    public ResponseEntity<DiagnosisResource> startDiagnosis(@RequestBody StartDiagnosisResource resource) {
        StartDiagnosisCommand command = StartDiagnosisCommandFromResourceAssembler.toCommandFromResource(resource);
        var diagnosisId = diagnosisCommandService.handle(command);
        cropDiseaseDetectionService.processDiagnosis(diagnosisId, resource.imageUrl());
        
        var diagnosis = diagnosisQueryService.getDiagnosisById(diagnosisId);
        
        if (diagnosis.isPresent()) {
            DiagnosisResource diagnosisResource = DiagnosisResourceFromEntityAssembler.toResourceFromEntity(diagnosis.get());
            return new ResponseEntity<>(diagnosisResource, HttpStatus.CREATED);
        }
        
        return ResponseEntity.badRequest().build();
    }    /**
     * Get diagnosis by ID
     */
    @GetMapping("/{diagnosisId}")
    public ResponseEntity<DiagnosisResource> getDiagnosisById(@PathVariable Long diagnosisId) {
        var diagnosis = diagnosisQueryService.getDiagnosisById(diagnosisId);
        
        if (diagnosis.isPresent()) {
            DiagnosisResource resource = DiagnosisResourceFromEntityAssembler.toResourceFromEntity(diagnosis.get());
            return ResponseEntity.ok(resource);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get diagnoses by crop ID
     */
    @GetMapping
    public ResponseEntity<List<DiagnosisResource>> getDiagnosesByCropId(@RequestParam Long cropId) {
        GetDiagnosesByCropIdQuery query = new GetDiagnosesByCropIdQuery(cropId);
        List<Diagnosis> diagnoses = diagnosisQueryService.handle(query);
        
        List<DiagnosisResource> resources = diagnoses.stream()
            .map(DiagnosisResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
            
        return ResponseEntity.ok(resources);
    }    /**
     * Get diagnoses by profile ID
     */
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<DiagnosisResource>> getDiagnosesByProfileId(@PathVariable Long profileId) {
        List<Diagnosis> diagnoses = diagnosisQueryService.getDiagnosesByProfileId(profileId);
        
        List<DiagnosisResource> resources = diagnoses.stream()
            .map(DiagnosisResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
            
        return ResponseEntity.ok(resources);
    }

    /**
     * Get pending diagnoses
     */
    @GetMapping("/pending")
    public ResponseEntity<List<DiagnosisResource>> getPendingDiagnoses() {
        List<Diagnosis> diagnoses = diagnosisQueryService.getPendingDiagnoses();
        
        List<DiagnosisResource> resources = diagnoses.stream()
            .map(DiagnosisResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
            
        return ResponseEntity.ok(resources);
    }

    /**
     * Get detection service status
     */
    @GetMapping("/detection-service/status")
    public ResponseEntity<Object> getDetectionServiceStatus() {
        boolean isAvailable = cropDiseaseDetectionService.isDetectionServiceAvailable();
        return ResponseEntity.ok(new ServiceStatusResource(isAvailable));
    }

    public record ServiceStatusResource(boolean available) {}
}
