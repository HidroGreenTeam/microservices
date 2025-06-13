package com.hidrogreen.report_service.reports.domain.model.aggregates;

import com.hidrogreen.report_service.reports.domain.model.valueobjects.DiagnosedDisease;
import com.hidrogreen.report_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends AuditableAbstractAggregateRoot<Report> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiagnosedDisease diagnosedDisease;

    @NotNull
    private Double accuracyPercentage;
}
