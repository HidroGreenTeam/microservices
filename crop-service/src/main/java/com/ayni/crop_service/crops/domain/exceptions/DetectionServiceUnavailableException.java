package com.ayni.crop_service.crops.domain.exceptions;

/**
 * Exception thrown when the detection service is unavailable
 */
public class DetectionServiceUnavailableException extends RuntimeException {
    public DetectionServiceUnavailableException(String message) {
        super("Detection service is unavailable: " + message);
    }

    public DetectionServiceUnavailableException(String message, Throwable cause) {
        super("Detection service is unavailable: " + message, cause);
    }
}
