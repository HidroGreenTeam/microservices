package com.hidrogreen.report_service.reports.domain.model.valueobjects;

public enum DiagnosedDisease {
    ROYA,
    MINERO,
    OJO_DE_ARANA;

    public static DiagnosedDisease fromString(String value) {
        return switch (value.toLowerCase()) {
            case "roya" -> ROYA;
            case "minero" -> MINERO;
            case "ojo de araña", "ojo_de_araña", "ojo_de_arana", "ojo de arana" -> OJO_DE_ARANA;
            default -> throw new IllegalArgumentException("Unknown disease: " + value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case ROYA -> "roya";
            case MINERO -> "minero";
            case OJO_DE_ARANA -> "ojo de araña";
        };
    }
}
