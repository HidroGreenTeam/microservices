from dataclasses import dataclass
from fastapi import UploadFile

@dataclass
class PredictDiseaseCommand:
    """Comando para predecir enfermedad sin guardar en base de datos"""
    
    file: UploadFile
    
    def __post_init__(self):
        """Validaciones del comando"""
        if not self.file:
            raise ValueError("file es requerido")
        
        # Validar tipo de archivo
        allowed_types = ["image/jpeg", "image/jpg", "image/png", "image/webp"]
        if self.file.content_type not in allowed_types:
            raise ValueError(f"Tipo de archivo no soportado. Tipos permitidos: {allowed_types}") 