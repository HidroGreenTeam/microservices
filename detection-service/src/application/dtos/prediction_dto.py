from dataclasses import dataclass
from typing import List, Tuple

@dataclass
class PredictionDTO:
    """DTO para transferir datos de predicción"""
    
    predicted_class: str
    confidence: float
    disease_detected: bool
    requires_treatment: bool
    top_predictions: List[Tuple[str, float]]
    
    @classmethod
    def from_value_object(cls, prediction_result):
        """Crea un DTO desde un PredictionResult"""
        return cls(
            predicted_class=prediction_result.predicted_class,
            confidence=prediction_result.confidence,
            disease_detected=prediction_result.disease_detected,
            requires_treatment=prediction_result.requires_treatment,
            top_predictions=prediction_result.top_predictions
        )
    
    def to_dict(self) -> dict:
        """Convierte el DTO a diccionario"""
        return {
            "predicted_class": self.predicted_class,
            "confidence": self.confidence,
            "disease_detected": self.disease_detected,
            "requires_treatment": self.requires_treatment,
            "top_predictions": [
                {"class": class_name, "confidence": confidence}
                for class_name, confidence in self.top_predictions
            ]
        } 