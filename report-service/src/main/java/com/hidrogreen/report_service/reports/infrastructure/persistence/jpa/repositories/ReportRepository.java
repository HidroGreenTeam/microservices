package com.hidrogreen.report_service.reports.infrastructure.persistence.jpa.repositories;

import com.hidrogreen.report_service.reports.domain.model.aggregates.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
