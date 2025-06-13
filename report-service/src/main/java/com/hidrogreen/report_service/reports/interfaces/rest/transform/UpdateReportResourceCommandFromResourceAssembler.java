package com.hidrogreen.report_service.reports.interfaces.rest.transform;

import com.hidrogreen.report_service.reports.domain.model.commands.UpdateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.valueobjects.DiagnosedDisease;
import com.hidrogreen.report_service.reports.interfaces.rest.resources.UpdateReportResource;

public class UpdateReportResourceCommandFromResourceAssembler {
    public static UpdateReportCommand toCommandFromResource(Long id, UpdateReportResource resource) {
        return new UpdateReportCommand(
                id,
                DiagnosedDisease.fromString(resource.diagnosedDisease()),
                resource.accuracyPercentage());
    }
}
