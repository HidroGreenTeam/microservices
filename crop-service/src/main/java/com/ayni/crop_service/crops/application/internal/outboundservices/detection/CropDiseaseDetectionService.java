package com.ayni.crop_service.crops.application.internal.outboundservices.detection;

import com.ayni.crop_service.crops.domain.model.commands.CompleteDiagnosisCommand;
import com.ayni.crop_service.crops.domain.services.DiagnosisCommandService;
import com.ayni.crop_service.crops.infrastructure.detection.DetectionServiceClient;
import com.ayni.crop_service.crops.domain.exceptions.DetectionServiceUnavailableException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Crop disease detection service - orchestrates detection analysis
 */
@Service
public class CropDiseaseDetectionService {

    private static final Logger log = LoggerFactory.getLogger(CropDiseaseDetectionService.class);
    
    private final DetectionServiceClient detectionServiceClient;
    private final DiagnosisCommandService diagnosisCommandService;

    public CropDiseaseDetectionService(DetectionServiceClient detectionServiceClient, 
                                     DiagnosisCommandService diagnosisCommandService) {
        this.detectionServiceClient = detectionServiceClient;
        this.diagnosisCommandService = diagnosisCommandService;
    }

    /**
     * Process diagnosis with detection service
     *
     * @param diagnosisId the diagnosis ID
     * @param imageUrl the image URL to analyze
     */
    public void processDiagnosis(Long diagnosisId, String imageUrl) {
        try {
            log.info("Processing diagnosis {} with image {}", diagnosisId, imageUrl);

            // Call detection service
            DetectionServiceClient.DetectionAnalysisResult result = 
                detectionServiceClient.analyzeImage(imageUrl);

            // Complete diagnosis with result
            CompleteDiagnosisCommand command = new CompleteDiagnosisCommand(
                diagnosisId,
                result.detectionResult().getDiseaseName(),
                result.detectionResult().isDiseaseDetected(),
                result.confidenceScore(),
                result.detectionResult().recommendations()
            );

            diagnosisCommandService.handle(command);

            log.info("Successfully completed diagnosis {}", diagnosisId);

        } catch (DetectionServiceUnavailableException e) {
            log.error("Detection service unavailable for diagnosis {}", diagnosisId, e);
            diagnosisCommandService.markAsFailed(diagnosisId, "Detection service unavailable: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing diagnosis {}", diagnosisId, e);
            diagnosisCommandService.markAsFailed(diagnosisId, "Processing error: " + e.getMessage());
        }
    }

    /**
     * Check if detection service is available
     *
     * @return true if service is available
     */
    public boolean isDetectionServiceAvailable() {
        return detectionServiceClient.isServiceAvailable();
    }
}
