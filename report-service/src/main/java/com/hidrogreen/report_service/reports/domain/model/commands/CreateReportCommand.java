package com.hidrogreen.report_service.reports.domain.model.commands;

import com.hidrogreen.report_service.reports.domain.model.valueobjects.DiagnosedDisease;

public record CreateReportCommand(
        DiagnosedDisease diagnosedDisease,
        Double accuracyPercentage) {
}
