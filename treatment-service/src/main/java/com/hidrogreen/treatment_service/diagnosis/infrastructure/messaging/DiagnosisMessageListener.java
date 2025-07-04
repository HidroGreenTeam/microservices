package com.hidrogreen.treatment_service.diagnosis.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.commands.CompleteDiagnosisCommand;
import com.hidrogreen.treatment_service.diagnosis.domain.services.DiagnosisCommandService;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.persistence.jpa.repositories.DiagnosisRepository;
import com.hidrogreen.treatment_service.diagnosis.infrastructure.clients.CropServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


@Component
public class DiagnosisMessageListener {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisMessageListener.class);
    
        private final DiagnosisCommandService diagnosisCommandService;
    private final DiagnosisRepository diagnosisRepository;
    private final CropServiceClient cropServiceClient;
    private final ObjectMapper objectMapper;

    public DiagnosisMessageListener(DiagnosisCommandService diagnosisCommandService, 
                                  DiagnosisRepository diagnosisRepository,
                                  CropServiceClient cropServiceClient,
                                  ObjectMapper objectMapper) {
        this.diagnosisCommandService = diagnosisCommandService;
        this.diagnosisRepository = diagnosisRepository;
        this.cropServiceClient = cropServiceClient;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "diagnosis_queue")
    public void handleDiagnosisMessage(String message) {
        try {
            log.info("=== RECEIVED DIAGNOSIS MESSAGE ===");
            log.info("Message: {}", message);
            
            
            Map<String, Object> diagnosisData = objectMapper.readValue(message, Map.class);
            
            Long cropId = getLongValue(diagnosisData, "crop_id");
            String predictedClass = (String) diagnosisData.get("predicted_class");
            Double confidence = getDoubleValue(diagnosisData, "confidence");
            Boolean diseaseDetected = (Boolean) diagnosisData.get("disease_detected");
            String imageUrl = (String) diagnosisData.get("image_url");
            Boolean requiresTreatment = (Boolean) diagnosisData.get("requires_treatment");
            
            log.info("Parsed diagnosis: cropId={}, disease={}, confidence={}, requiresTreatment={}", 
                    cropId, predictedClass, confidence, requiresTreatment);
            
            
            if (!validateCropExists(cropId)) {
                log.error("❌ Crop with ID {} does not exist. Skipping diagnosis creation.", cropId);
                return;
            }
            
            
            Diagnosis newDiagnosis = new Diagnosis(cropId, imageUrl);
            
            
            Diagnosis savedDiagnosis = diagnosisRepository.save(newDiagnosis);
            Long finalDiagnosisId = savedDiagnosis.getId();
            
            log.info("Created new diagnosis with Detection Service ID: {}", finalDiagnosisId);
            
            
            CompleteDiagnosisCommand completeCommand = new CompleteDiagnosisCommand(
                finalDiagnosisId,
                predictedClass,
                diseaseDetected != null ? diseaseDetected : false,
                confidence,
                generateRecommendations(predictedClass, confidence)
            );
            
            Optional<Diagnosis> completedDiagnosis = diagnosisCommandService.handle(completeCommand);
            
            if (completedDiagnosis.isPresent()) {
                log.info("✅ Successfully processed diagnosis {} for crop {} - Disease: {} ({}%)", 
                        finalDiagnosisId, cropId, predictedClass, 
                        confidence != null ? String.format("%.1f", confidence * 100) : "N/A");
                
                
                
                
            } else {
                log.error("❌ Failed to complete diagnosis {}", finalDiagnosisId);
            }
            
        } catch (Exception e) {
            log.error("❌ Error processing diagnosis message: {}", e.getMessage(), e);
        }
    }
    
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
    
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
    
    private String generateRecommendations(String predictedClass, Double confidence) {
        if (predictedClass == null) return "No se pudo determinar recomendaciones";
        
        return switch (predictedClass.toLowerCase()) {
            case "rust", "roya" -> "Aplicar fungicida específico para roya. Mejorar ventilación del cultivo.";
            case "miner", "minador" -> "Aplicar insecticida para minadores de hojas. Eliminar hojas afectadas.";
            case "phoma" -> "Tratamiento con fungicida sistémico. Evitar exceso de humedad.";
            case "redspider", "araña roja" -> "Aplicar acaricida específico. Aumentar humedad ambiental.";
            case "nodisease", "healthy" -> "Mantener prácticas de manejo actuales. Monitoreo preventivo.";
            default -> "Consultar con especialista agrícola para tratamiento específico.";
        };
    }

    
    private boolean validateCropExists(Long cropId) {
        try {
            log.info("🔍 Validating crop existence for ID: {}", cropId);
            CropServiceClient.CropDTO crop = cropServiceClient.getCropById(cropId);
            
            if (crop != null && crop.cropName() != null && !crop.cropName().trim().isEmpty()) {
                log.info("✅ Crop {} exists with name: {}", cropId, crop.cropName());
                return true;
            } else {
                log.warn("⚠️ Crop {} exists but has no name", cropId);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error validating crop {}: {}", cropId, e.getMessage());
            return false;
        }
    }
} 