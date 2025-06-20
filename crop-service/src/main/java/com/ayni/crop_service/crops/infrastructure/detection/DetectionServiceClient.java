package com.ayni.crop_service.crops.infrastructure.detection;

import com.ayni.crop_service.crops.domain.model.valueobjects.DetectionResult;
import com.ayni.crop_service.crops.domain.exceptions.DetectionServiceUnavailableException;
import com.ayni.crop_service.crops.domain.exceptions.LowConfidenceDetectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

/**
 * Detection service client - integrates with external DETECTION-SERVICE
 */
@Service
public class DetectionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DetectionServiceClient.class);

    private final RestTemplate restTemplate;
    private final String detectionServiceUrl;
    private final Double confidenceThreshold;

    public DetectionServiceClient(RestTemplate restTemplate,
                                @Value("${detection.service.url}") String detectionServiceUrl,
                                @Value("${detection.service.confidence.threshold:0.7}") Double confidenceThreshold) {
        this.restTemplate = restTemplate;
        this.detectionServiceUrl = detectionServiceUrl;
        this.confidenceThreshold = confidenceThreshold;
    }

    /**
     * Analyze crop image for disease detection
     *
     * @param imageUrl the image URL to analyze
     * @return the detection result
     */
    public DetectionAnalysisResult analyzeImage(String imageUrl) {
        try {
            log.info("Sending image analysis request to detection service: {}", imageUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                "imageUrl", imageUrl,
                "analysisType", "DISEASE_DETECTION"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<DetectionServiceResponse> response = restTemplate.postForEntity(
                detectionServiceUrl + "/api/v1/analyze",
                request,
                DetectionServiceResponse.class
            );            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                DetectionServiceResponse serviceResponse = response.getBody();
                
                // Additional null check for safety
                if (serviceResponse == null) {
                    throw new DetectionServiceUnavailableException("Invalid response from detection service");
                }
                
                // Validate confidence score
                if (serviceResponse.confidenceScore() != null && serviceResponse.confidenceScore() < confidenceThreshold) {
                    throw new LowConfidenceDetectionException(serviceResponse.confidenceScore(), confidenceThreshold);
                }

                DetectionResult detectionResult = new DetectionResult(
                    serviceResponse.detectedDisease(),
                    serviceResponse.diseaseDetected(),
                    serviceResponse.recommendations()
                );

                return new DetectionAnalysisResult(detectionResult, serviceResponse.confidenceScore());
            } else {
                throw new DetectionServiceUnavailableException("Invalid response from detection service");
            }

        } catch (ResourceAccessException e) {
            log.error("Failed to connect to detection service", e);
            throw new DetectionServiceUnavailableException("Cannot connect to detection service", e);
        } catch (Exception e) {
            log.error("Error analyzing image with detection service", e);
            throw new DetectionServiceUnavailableException("Error analyzing image: " + e.getMessage(), e);
        }
    }

    /**
     * Check if detection service is available
     *
     * @return true if service is available
     */
    public boolean isServiceAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                detectionServiceUrl + "/api/v1/health",
                String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("Detection service health check failed", e);
            return false;
        }
    }

    /**
     * Detection service response DTO
     */
    public record DetectionServiceResponse(
        String detectedDisease,
        boolean diseaseDetected,
        Double confidenceScore,
        String recommendations,
        String analysisId
    ) {}

    /**
     * Detection analysis result
     */
    public record DetectionAnalysisResult(
        DetectionResult detectionResult,
        Double confidenceScore
    ) {}
}
