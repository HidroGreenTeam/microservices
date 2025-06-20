package com.ayni.crop_service.crops.domain.exceptions;

/**
 * Exception thrown when detection confidence is too low
 */
public class LowConfidenceDetectionException extends RuntimeException {
    public LowConfidenceDetectionException(Double confidence, Double threshold) {
        super("Detection confidence " + confidence + " is below threshold " + threshold);
    }
}
