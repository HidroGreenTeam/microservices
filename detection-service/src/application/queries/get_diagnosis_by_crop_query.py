from dataclasses import dataclass

@dataclass
class GetDiagnosisByCropQuery:
    """Consulta para obtener diagnósticos de un cultivo específico"""
    
    crop_id: int
    
    def __post_init__(self):
        """Validaciones de la consulta"""
        if self.crop_id <= 0:
            raise ValueError("crop_id debe ser mayor que 0") 