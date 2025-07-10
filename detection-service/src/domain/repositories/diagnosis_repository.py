from abc import ABC, abstractmethod
from typing import List, Optional
from datetime import datetime
from ...domain.entities.diagnosis import Diagnosis

class DiagnosisRepository(ABC):
    """Interfaz del repositorio para la entidad Diagnosis"""
    
    @abstractmethod
    async def save(self, diagnosis: Diagnosis) -> Diagnosis:
        """Guarda un diagnóstico en el repositorio"""
        pass
    
    @abstractmethod
    async def find_by_id(self, diagnosis_id: int) -> Optional[Diagnosis]:
        """Busca un diagnóstico por su ID"""
        pass
    
    @abstractmethod
    async def find_by_profile_id(self, profile_id: int) -> List[Diagnosis]:
        """Busca todos los diagnósticos de un perfil específico"""
        pass
    
    @abstractmethod
    async def find_by_crop_id(self, crop_id: int) -> List[Diagnosis]:
        """Busca todos los diagnósticos de un cultivo específico"""
        pass
    
    @abstractmethod
    async def find_all(self) -> List[Diagnosis]:
        """Obtiene todos los diagnósticos"""
        pass
    
    @abstractmethod
    async def find_diseases_detected(self) -> List[Diagnosis]:
        """Obtiene todos los diagnósticos donde se detectó enfermedad"""
        pass
    
    @abstractmethod
    async def find_requiring_treatment(self) -> List[Diagnosis]:
        """Obtiene todos los diagnósticos que requieren tratamiento"""
        pass
    
    @abstractmethod
    async def count_total_diagnoses(self) -> int:
        """Cuenta el total de diagnósticos"""
        pass
    
    @abstractmethod
    async def count_diseases_detected(self) -> int:
        """Cuenta el total de diagnósticos con enfermedades detectadas"""
        pass
    
    @abstractmethod
    async def count_requiring_treatment(self) -> int:
        """Cuenta el total de diagnósticos que requieren tratamiento"""
        pass
    
    @abstractmethod
    async def get_disease_statistics(self) -> dict:
        """Obtiene estadísticas de distribución de enfermedades"""
        pass 