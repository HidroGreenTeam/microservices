package com.hidrogreen.report_service.reports.interfaces.rest.transform;

import com.hidrogreen.report_service.reports.domain.model.aggregates.Report;
import com.hidrogreen.report_service.reports.interfaces.rest.resources.ReportResource;

public class ReportResourceFromEntityAssembler {
    public static ReportResource toResourceFromEntity(Report report) {
        return new ReportResource(
                report.getId(),
                report.getDiagnosedDisease().toString(),
                report.getAccuracyPercentage());
    }
}
