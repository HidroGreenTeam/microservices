package com.hidrogreen.treatment_service.shared.domain.exceptions;

public class ExternalServiceException extends RuntimeException {
    
    private final String serviceName;
    private final String operation;
    
    public ExternalServiceException(String serviceName, String operation, String message) {
        super(String.format("Error communicating with %s service during %s: %s", serviceName, operation, message));
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public ExternalServiceException(String serviceName, String operation, Throwable cause) {
        super(String.format("Error communicating with %s service during %s", serviceName, operation), cause);
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