package com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources;

import java.util.List;


public record DiagnosisHistoryListResource(
    List<DiagnosisHistoryResource> diagnosis
) {} 