package com.hidrogreen.report_service.reports.application.internal.queryServiceImpl;

import com.hidrogreen.report_service.reports.domain.model.aggregates.Report;
import com.hidrogreen.report_service.reports.domain.model.queries.ReportQueryService;
import com.hidrogreen.report_service.reports.infrastructure.persistence.jpa.repositories.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportQueryServiceImpl implements ReportQueryService {
    private final ReportRepository reportRepository;

    public ReportQueryServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }
}
