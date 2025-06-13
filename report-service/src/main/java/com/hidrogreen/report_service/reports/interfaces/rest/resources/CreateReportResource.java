package com.hidrogreen.report_service.reports.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record CreateReportResource(
        @NotNull String diagnosedDisease,
        @NotNull Double accuracyPercentage) {
}
