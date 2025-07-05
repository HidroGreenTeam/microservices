package com.ayni.crop_service.shared.domain.exceptions;

public class ExternalServiceException extends RuntimeException {
    private final String serviceName;
    private final String operation;

    public ExternalServiceException(String serviceName, String operation, Throwable cause) {
        super(String.format("Error communicating with %s during %s: %s", serviceName, operation, cause.getMessage()), cause);
        this.serviceName = serviceName;
        this.operation = operation;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getOperation() {
        return operation;
    }
} 