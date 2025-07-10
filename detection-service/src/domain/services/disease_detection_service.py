from abc import ABC, abstractmethod
from typing import List
import numpy as np
from PIL import Image
from ...domain.value_objects.prediction_result import PredictionResult

class DiseaseDetectionService(ABC):
    """Servicio de dominio para la detección de enfermedades en plantas"""
    
    @abstractmethod
    async def predict_disease(self, image: Image.Image) -> PredictionResult:
        """
        Predice la enfermedad en una imagen de planta
        
        Args:
            image: Imagen de la planta a analizar
            
        Returns:
            PredictionResult con la predicción y confianza
        """
        pass
    
    @abstractmethod
    async def preprocess_image(self, image: Image.Image, target_size: tuple = (480, 480)) -> np.ndarray:
        """
        Preprocesa una imagen para el modelo de predicción
        
        Args:
            image: Imagen a preprocesar
            target_size: Tamaño objetivo para redimensionar
            
        Returns:
            Array numpy preprocesado
        """
        pass
    
    @abstractmethod
    def get_supported_diseases(self) -> List[str]:
        """
        Obtiene la lista de enfermedades soportadas por el modelo
        
        Returns:
            Lista de nombres de enfermedades
        """
        pass 