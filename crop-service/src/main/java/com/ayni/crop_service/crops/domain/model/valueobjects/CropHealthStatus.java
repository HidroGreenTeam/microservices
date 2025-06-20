package com.ayni.crop_service.crops.domain.model.valueobjects;

/**
 * Crop health status enum
 */
public enum CropHealthStatus {
    HEALTHY, 
    AT_RISK, 
    DISEASED, 
    CRITICAL, 
    UNKNOWN;
    
    public boolean isHealthy() {
        return this == HEALTHY;
    }
    
    public boolean isAtRisk() {
        return this == AT_RISK;
    }
    
    public boolean isDiseased() {
        return this == DISEASED;
    }
    
    public boolean isCritical() {
        return this == CRITICAL;
    }
    
    public boolean isUnknown() {
        return this == UNKNOWN;
    }
    
    public int getHealthScore() {
        return switch (this) {
            case HEALTHY -> 100;
            case AT_RISK -> 70;
            case DISEASED -> 40;
            case CRITICAL -> 10;
            case UNKNOWN -> 50;
        };
    }
    
    public String getDisplayName() {
        return switch (this) {
            case HEALTHY -> "Healthy";
            case AT_RISK -> "At Risk";
            case DISEASED -> "Diseased";
            case CRITICAL -> "Critical";
            case UNKNOWN -> "Unknown";
        };
    }
}
