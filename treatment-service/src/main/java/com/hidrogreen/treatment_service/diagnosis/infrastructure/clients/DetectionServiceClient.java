package com.hidrogreen.treatment_service.diagnosis.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "detection-service", url = "${detection.service.url:http://localhost:8000}")
public interface DetectionServiceClient {

    @PostMapping(value = "/api/v1/detections/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    DetectionResult predictDisease(@RequestPart("image") MultipartFile file);

    record DetectionResult(
            String predicted_class,
            Double confidence,
            Boolean disease_detected,
            Boolean requires_treatment
    ) {}
}
