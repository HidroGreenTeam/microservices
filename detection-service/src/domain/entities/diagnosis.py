from dataclasses import dataclass
from datetime import datetime
from typing import Optional
from enum import Enum

class DiseaseType(Enum):
    MINER = "miner"
    NODISEASE = "nodisease"
    PHOMA = "phoma"
    REDSPIDER = "redspider"
    RUST = "rust"

@dataclass
class Diagnosis:
    """Entidad de dominio para representar un diagnóstico de enfermedad de planta"""
    
    id: Optional[int]
    crop_id: int
    profile_id: int
    predicted_class: str
    confidence: float
    disease_detected: bool
    requires_treatment: bool
    image_url: str
    image_public_id: Optional[str]
    created_at: datetime
    updated_at: datetime
    
    def __post_init__(self):
        """Validaciones de dominio después de la inicialización"""
        if self.crop_id <= 0:
            raise ValueError("crop_id debe ser mayor que 0")
        
        if self.profile_id <= 0:
            raise ValueError("profile_id debe ser mayor que 0")
        
        if not (0.0 <= self.confidence <= 1.0):
            raise ValueError("confidence debe estar entre 0.0 y 1.0")
        
        if not self.image_url:
            raise ValueError("image_url no puede estar vacío")
        
        # Validar que predicted_class sea válido
        valid_classes = [disease.value for disease in DiseaseType]
        if self.predicted_class not in valid_classes:
            raise ValueError(f"predicted_class debe ser uno de: {valid_classes}")
    
    @property
    def is_healthy(self) -> bool:
        """Determina si la planta está sana"""
        return self.predicted_class == DiseaseType.NODISEASE.value
    
    @property
    def severity_level(self) -> str:
        """Determina el nivel de severidad basado en la confianza"""
        if self.confidence >= 0.95:
            return "CRITICAL"
        elif self.confidence >= 0.85:
            return "HIGH"
        elif self.confidence >= 0.70:
            return "MEDIUM"
        else:
            return "LOW"
    
    def should_notify_treatment_service(self) -> bool:
        """Determina si se debe notificar al servicio de tratamiento"""
        return self.disease_detected and self.requires_treatment
    
    def to_dict(self) -> dict:
        """Convierte la entidad a diccionario para serialización"""
        return {
            "id": self.id,
            "crop_id": self.crop_id,
            "profile_id": self.profile_id,
            "predicted_class": self.predicted_class,
            "confidence": self.confidence,
            "disease_detected": self.disease_detected,
            "requires_treatment": self.requires_treatment,
            "image_url": self.image_url,
            "image_public_id": self.image_public_id,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        } 