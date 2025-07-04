package com.hidrogreen.report_service.reports.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record CreateReportResource(
        @NotNull Long farmerId,
        @NotNull String diagnosedDisease,
        @NotNull Double accuracyPercentage) {
}
