package com.hidrogreen.report_service.reports.interfaces.rest.controllers;

import com.hidrogreen.report_service.reports.application.internal.commandServiceImpl.ReportCommandServiceImpl;
import com.hidrogreen.report_service.reports.application.internal.queryServiceImpl.ReportQueryServiceImpl;
import com.hidrogreen.report_service.reports.domain.model.commands.DeleteReportCommand;
import com.hidrogreen.report_service.reports.interfaces.rest.resources.CreateReportResource;
import com.hidrogreen.report_service.reports.interfaces.rest.resources.ReportResource;
import com.hidrogreen.report_service.reports.interfaces.rest.resources.UpdateReportResource;
import com.hidrogreen.report_service.reports.interfaces.rest.transform.CreateReportResourceCommandFromResourceAssembler;
import com.hidrogreen.report_service.reports.interfaces.rest.transform.ReportResourceFromEntityAssembler;
import com.hidrogreen.report_service.reports.interfaces.rest.transform.UpdateReportResourceCommandFromResourceAssembler;
import com.hidrogreen.report_service.shared.interfaces.rest.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportsController {
    private final ReportCommandServiceImpl reportCommandService;
    private final ReportQueryServiceImpl reportQueryService;

    public ReportsController(ReportCommandServiceImpl reportCommandService, ReportQueryServiceImpl reportQueryService) {
        this.reportCommandService = reportCommandService;
        this.reportQueryService = reportQueryService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody @Valid CreateReportResource resource) {
        try {
            var command = CreateReportResourceCommandFromResourceAssembler.toCommandFromResource(resource);
            Long id = reportCommandService.handle(command);
            var reportOpt = reportQueryService.getReportById(id);
            if (reportOpt.isPresent()) {
                return ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(reportOpt.get()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Creation failed", "Failed to create report"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Creation failed", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ReportResource>> getAllReports() {
        List<ReportResource> reports = reportQueryService.getAllReports().stream()
                .map(ReportResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        var reportOpt = reportQueryService.getReportById(id);
        if (reportOpt.isPresent()) {
            return ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(reportOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Resource not found", "Report with id " + id + " not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Long id,
            @RequestBody @Valid UpdateReportResource resource) {
        try {
            var command = UpdateReportResourceCommandFromResourceAssembler.toCommandFromResource(id, resource);
            Optional<com.hidrogreen.report_service.reports.domain.model.aggregates.Report> updated = reportCommandService
                    .handle(command);
            if (updated.isPresent()) {
                return ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(updated.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Resource not found", "Report with id " + id + " not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Update failed", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            reportCommandService.handle(new DeleteReportCommand(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Deletion failed", e.getMessage()));
        }
    }
}
