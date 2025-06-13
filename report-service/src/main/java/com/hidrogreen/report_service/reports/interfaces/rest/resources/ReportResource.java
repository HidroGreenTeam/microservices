package com.hidrogreen.report_service.reports.interfaces.rest.resources;

public record ReportResource(
        Long id,
        String diagnosedDisease,
        Double accuracyPercentage) {
}
