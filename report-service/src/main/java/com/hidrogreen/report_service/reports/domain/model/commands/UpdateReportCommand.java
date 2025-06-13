package com.hidrogreen.report_service.reports.domain.model.commands;

import com.hidrogreen.report_service.reports.domain.model.valueobjects.DiagnosedDisease;

public record UpdateReportCommand(
        Long id,
        DiagnosedDisease diagnosedDisease,
        Double accuracyPercentage) {
}
