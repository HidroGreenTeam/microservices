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
import jakarta.validation.Valid;
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
    public ResponseEntity<ReportResource> createReport(@RequestBody @Valid CreateReportResource resource) {
        var command = CreateReportResourceCommandFromResourceAssembler.toCommandFromResource(resource);
        Long id = reportCommandService.handle(command);
        return reportQueryService.getReportById(id)
                .map(report -> ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(report)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ReportResource>> getAllReports() {
        List<ReportResource> reports = reportQueryService.getAllReports().stream()
                .map(ReportResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResource> getReportById(@PathVariable Long id) {
        return reportQueryService.getReportById(id)
                .map(report -> ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(report)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResource> updateReport(@PathVariable Long id,
            @RequestBody @Valid UpdateReportResource resource) {
        var command = UpdateReportResourceCommandFromResourceAssembler.toCommandFromResource(id, resource);
        Optional<com.hidrogreen.report_service.reports.domain.model.aggregates.Report> updated = reportCommandService
                .handle(command);
        return updated
                .map(report -> ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(report)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        reportCommandService.handle(new DeleteReportCommand(id));
        return ResponseEntity.ok().build();
    }
}
