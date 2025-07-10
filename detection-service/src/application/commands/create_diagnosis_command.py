from dataclasses import dataclass
from typing import Optional
from fastapi import UploadFile

@dataclass
class CreateDiagnosisCommand:
    """Comando para crear un nuevo diagnóstico"""
    
    file: UploadFile
    crop_id: int
    profile_id: int
    
    def __post_init__(self):
        """Validaciones del comando"""
        if self.crop_id <= 0:
            raise ValueError("crop_id debe ser mayor que 0")
        
        if self.profile_id <= 0:
            raise ValueError("profile_id debe ser mayor que 0")
        
        if not self.file:
            raise ValueError("file es requerido")
        
        # Validar tipo de archivo
        allowed_types = ["image/jpeg", "image/jpg", "image/png", "image/webp"]
        if self.file.content_type not in allowed_types:
            raise ValueError(f"Tipo de archivo no soportado. Tipos permitidos: {allowed_types}") 