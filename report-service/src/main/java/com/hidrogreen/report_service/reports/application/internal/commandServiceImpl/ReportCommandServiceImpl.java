package com.hidrogreen.report_service.reports.application.internal.commandServiceImpl;

import com.hidrogreen.report_service.reports.domain.model.aggregates.Report;
import com.hidrogreen.report_service.reports.domain.model.commands.CreateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.commands.UpdateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.commands.DeleteReportCommand;
import com.hidrogreen.report_service.reports.domain.services.ReportCommandService;
import com.hidrogreen.report_service.reports.infrastructure.persistence.jpa.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReportCommandServiceImpl implements ReportCommandService {
    private final ReportRepository reportRepository;

    public ReportCommandServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    @Transactional
    public Long handle(CreateReportCommand command) {
        Report report = Report.builder()
                .diagnosedDisease(command.diagnosedDisease())
                .accuracyPercentage(command.accuracyPercentage())
                .build();
        return reportRepository.save(report).getId();
    }

    @Override
    @Transactional
    public Optional<Report> handle(UpdateReportCommand command) {
        Optional<Report> optionalReport = reportRepository.findById(command.id());
        if (optionalReport.isPresent()) {
            Report report = optionalReport.get();
            report.setDiagnosedDisease(command.diagnosedDisease());
            report.setAccuracyPercentage(command.accuracyPercentage());
            return Optional.of(reportRepository.save(report));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void handle(DeleteReportCommand command) {
        reportRepository.deleteById(command.id());
    }
}
