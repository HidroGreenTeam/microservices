from dataclasses import dataclass
from typing import Dict

@dataclass
class StatisticsDTO:
    """DTO para transferir datos de estadísticas"""
    
    total_diagnoses: int
    diseases_detected: int
    treatments_required: int
    healthy_crops: int
    disease_distribution: Dict[str, int]
    
    def to_dict(self) -> dict:
        """Convierte el DTO a diccionario"""
        return {
            "total_diagnoses": self.total_diagnoses,
            "diseases_detected": self.diseases_detected,
            "treatments_required": self.treatments_required,
            "healthy_crops": self.healthy_crops,
            "disease_distribution": self.disease_distribution
        } 