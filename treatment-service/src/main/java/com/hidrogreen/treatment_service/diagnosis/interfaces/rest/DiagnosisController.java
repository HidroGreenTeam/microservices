package com.hidrogreen.treatment_service.diagnosis.interfaces.rest;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.queries.GetDiagnosisByCropIdQuery;
import com.hidrogreen.treatment_service.diagnosis.domain.services.DiagnosisQueryService;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources.DiagnosisResource;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources.DiagnosisHistoryResource;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources.DiagnosisHistoryListResource;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.transform.DiagnosisResourceFromEntityAssembler;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.transform.DiagnosisHistoryResourceFromEntityAssembler;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.CropServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/v1/diagnosis/", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Diagnosis", description = "Plant disease diagnosis management API")
@CrossOrigin(origins = "*")
public class DiagnosisController {

    private final DiagnosisQueryService diagnosisQueryService;
    private final CropServiceClient cropServiceClient;

    public DiagnosisController(DiagnosisQueryService diagnosisQueryService, 
                             CropServiceClient cropServiceClient) {
        this.diagnosisQueryService = diagnosisQueryService;
        this.cropServiceClient = cropServiceClient;
    }

    
    @Operation(summary = "Get farmer diagnosis history", description = "Get ALL diagnosis history for a specific farmer")
    @GetMapping("{farmerId}")
    public ResponseEntity<DiagnosisHistoryListResource> getFarmerDiagnosisHistory(@PathVariable Long farmerId) {
        
        
        List<Diagnosis> diagnosis = diagnosisQueryService.getDiagnosisByProfileId(farmerId);
        
        
        List<DiagnosisHistoryResource> historyResources = diagnosis.stream()
            .map(d -> {
                String cropName = getCropName(d.getCropId());
                return DiagnosisHistoryResourceFromEntityAssembler.toResourceFromEntity(d, cropName);
            })
            .collect(Collectors.toList());
        
        DiagnosisHistoryListResource response = new DiagnosisHistoryListResource(historyResources);
        return ResponseEntity.ok(response);
    }

    
    @Operation(summary = "Get crop diagnosis", description = "Get all diagnosis for a specific crop")
    @GetMapping("crop/{cropId}")
    public ResponseEntity<DiagnosisHistoryListResource> getCropDiagnosis(@PathVariable Long cropId) {
        
        GetDiagnosisByCropIdQuery query = new GetDiagnosisByCropIdQuery(cropId);
        List<Diagnosis> diagnosis = diagnosisQueryService.handle(query);
        
        
        List<DiagnosisHistoryResource> historyResources = diagnosis.stream()
            .map(d -> {
                String cropName = getCropName(d.getCropId());
                return DiagnosisHistoryResourceFromEntityAssembler.toResourceFromEntity(d, cropName);
            })
            .collect(Collectors.toList());
       
        DiagnosisHistoryListResource response = new DiagnosisHistoryListResource(historyResources);
        return ResponseEntity.ok(response);
    }

    
    @Operation(summary = "Get diagnosis by ID", description = "Get specific diagnosis details by its ID")
    @GetMapping("detail/{diagnosisId}")
    public ResponseEntity<DiagnosisResource> getDiagnosisById(@PathVariable Long diagnosisId) {
        var diagnosis = diagnosisQueryService.getDiagnosisById(diagnosisId);
        
        if (diagnosis.isPresent()) {
            DiagnosisResource resource = DiagnosisResourceFromEntityAssembler.toResourceFromEntity(diagnosis.get());
            return ResponseEntity.ok(resource);
        }
        
        return ResponseEntity.notFound().build();
    }



    
    private String getCropName(Long cropId) {
        try {
            CropServiceClient.CropDTO crop = cropServiceClient.getCropById(cropId);
            return crop.cropName();
        } catch (Exception e) {
            
            return "Cultivo " + cropId;
        }
    }
} 