package com.hidrogreen.report_service.reports.domain.services;

import com.hidrogreen.report_service.reports.domain.model.aggregates.Report;
import com.hidrogreen.report_service.reports.domain.model.commands.CreateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.commands.UpdateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.commands.DeleteReportCommand;

import java.util.Optional;

public interface ReportCommandService {
    Long handle(CreateReportCommand command);

    Optional<Report> handle(UpdateReportCommand command);

    void handle(DeleteReportCommand command);
}
