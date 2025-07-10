from dataclasses import dataclass

@dataclass
class GetDiagnosisByIdQuery:
    """Consulta para obtener un diagnóstico por su ID"""
    
    diagnosis_id: int
    
    def __post_init__(self):
        """Validaciones de la consulta"""
        if self.diagnosis_id <= 0:
            raise ValueError("diagnosis_id debe ser mayor que 0") 