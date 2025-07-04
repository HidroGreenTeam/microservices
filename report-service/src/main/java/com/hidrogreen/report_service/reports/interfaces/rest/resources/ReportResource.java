package com.hidrogreen.report_service.reports.interfaces.rest.resources;

public record ReportResource(
        Long id,
        Long farmerId,
        String diagnosedDisease,
        Double accuracyPercentage) {
}
