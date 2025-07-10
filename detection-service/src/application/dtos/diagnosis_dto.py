from dataclasses import dataclass
from typing import Optional
from datetime import datetime

@dataclass
class DiagnosisDTO:
    """DTO para transferir datos de Diagnosis"""
    
    diagnosis_id: Optional[int]
    crop_id: int
    profile_id: int
    predicted_class: str
    confidence: float
    disease_detected: bool
    requires_treatment: bool
    image_url: str
    image_public_id: Optional[str]
    created_at: Optional[str]
    updated_at: Optional[str]
    
    @classmethod
    def from_entity(cls, diagnosis):
        """Crea un DTO desde una entidad Diagnosis"""
        return cls(
            diagnosis_id=diagnosis.id,
            crop_id=diagnosis.crop_id,
            profile_id=diagnosis.profile_id,
            predicted_class=diagnosis.predicted_class,
            confidence=diagnosis.confidence,
            disease_detected=diagnosis.disease_detected,
            requires_treatment=diagnosis.requires_treatment,
            image_url=diagnosis.image_url,
            image_public_id=diagnosis.image_public_id,
            created_at=diagnosis.created_at.isoformat() if diagnosis.created_at else None,
            updated_at=diagnosis.updated_at.isoformat() if diagnosis.updated_at else None
        )
    
    def to_dict(self) -> dict:
        """Convierte el DTO a diccionario"""
        return {
            "diagnosis_id": self.diagnosis_id,
            "crop_id": self.crop_id,
            "profile_id": self.profile_id,
            "predicted_class": self.predicted_class,
            "confidence": self.confidence,
            "disease_detected": self.disease_detected,
            "requires_treatment": self.requires_treatment,
            "image_url": self.image_url,
            "image_public_id": self.image_public_id,
            "created_at": self.created_at,
            "updated_at": self.updated_at
        } 