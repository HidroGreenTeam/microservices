package com.hidrogreen.report_service.reports.application.internal.commandServiceImpl;

import com.hidrogreen.report_service.reports.domain.model.aggregates.Report;
import com.hidrogreen.report_service.reports.domain.model.commands.CreateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.commands.UpdateReportCommand;
import com.hidrogreen.report_service.reports.domain.model.commands.DeleteReportCommand;
import com.hidrogreen.report_service.reports.domain.services.ReportCommandService;
import com.hidrogreen.report_service.reports.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.hidrogreen.report_service.shared.infrastructure.clients.CropServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.List;

@Service
public class ReportCommandServiceImpl implements ReportCommandService {
    
    private static final Logger log = LoggerFactory.getLogger(ReportCommandServiceImpl.class);
    
    private final ReportRepository reportRepository;
    private final CropServiceClient cropServiceClient;

    public ReportCommandServiceImpl(ReportRepository reportRepository, CropServiceClient cropServiceClient) {
        this.reportRepository = reportRepository;
        this.cropServiceClient = cropServiceClient;
    }

    @Override
    @Transactional
    public Long handle(CreateReportCommand command) {
        
        try {
            List<CropServiceClient.CropResponse> crops = cropServiceClient.getCropsFromFarmer(command.farmerId());
            if (crops == null || crops.isEmpty()) {
                log.warn("Farmer not found or has no crops with id: {}", command.farmerId());
                throw new IllegalArgumentException("Farmer not found with id: " + command.farmerId());
            }
        } catch (Exception e) {
            log.error("Error validating farmer with id: {}: {}", command.farmerId(), e.getMessage());
            throw new IllegalArgumentException("Farmer not found with id: " + command.farmerId());
        }
        
        Report report = Report.builder()
                .farmerId(command.farmerId())
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
        
        if (!reportRepository.existsById(command.id())) {
            log.warn("Report not found with id: {}", command.id());
            throw new IllegalArgumentException("Report not found with id: " + command.id());
        }
        reportRepository.deleteById(command.id());
    }
}
