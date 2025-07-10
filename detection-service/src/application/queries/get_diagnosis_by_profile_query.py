from dataclasses import dataclass

@dataclass
class GetDiagnosisByProfileQuery:
    """Consulta para obtener diagnósticos de un perfil específico"""
    
    profile_id: int
    
    def __post_init__(self):
        """Validaciones de la consulta"""
        if self.profile_id <= 0:
            raise ValueError("profile_id debe ser mayor que 0") 