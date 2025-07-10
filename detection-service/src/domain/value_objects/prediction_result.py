from dataclasses import dataclass
from typing import List, Tuple
import numpy as np

@dataclass(frozen=True)
class PredictionResult:
    """Value Object para representar el resultado de una predicción"""
    
    predicted_class: str
    confidence: float
    all_predictions: List[float]
    
    def __post_init__(self):
        """Validaciones después de la inicialización"""
        if not (0.0 <= self.confidence <= 1.0):
            raise ValueError("confidence debe estar entre 0.0 y 1.0")
        
        if not self.predicted_class:
            raise ValueError("predicted_class no puede estar vacío")
    
    @property
    def disease_detected(self) -> bool:
        """Determina si se detectó una enfermedad"""
        return self.predicted_class != "nodisease"
    
    @property
    def requires_treatment(self) -> bool:
        """Determina si requiere tratamiento basado en la confianza"""
        return self.disease_detected and self.confidence > 0.90
    
    @property
    def top_predictions(self, top_k: int = 3) -> List[Tuple[str, float]]:
        """Obtiene las top-k predicciones con sus clases"""
        class_names = ['miner', 'nodisease', 'phoma', 'redspider', 'rust']
        predictions_with_classes = list(zip(class_names, self.all_predictions))
        sorted_predictions = sorted(predictions_with_classes, key=lambda x: x[1], reverse=True)
        return sorted_predictions[:top_k]
    
    def to_dict(self) -> dict:
        """Convierte el resultado a diccionario"""
        return {
            "predicted_class": self.predicted_class,
            "confidence": self.confidence,
            "disease_detected": self.disease_detected,
            "requires_treatment": self.requires_treatment,
            "top_predictions": self.top_predictions
        } 