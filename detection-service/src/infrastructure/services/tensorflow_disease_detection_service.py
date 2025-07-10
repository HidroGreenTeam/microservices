import os
import logging
from typing import List
import numpy as np
from PIL import Image
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image

from ...domain.services.disease_detection_service import DiseaseDetectionService
from ...domain.value_objects.prediction_result import PredictionResult

logger = logging.getLogger(__name__)

class TensorFlowDiseaseDetectionService(DiseaseDetectionService):
    """Implementación del servicio de detección de enfermedades usando TensorFlow"""
    
    def __init__(self, model_path: str = None):
        self.model_path = model_path or os.path.join("model", "model-91840.keras")
        self.model = None
        self.class_names = ['miner', 'nodisease', 'phoma', 'redspider', 'rust']
        self._load_model()
    
    def _load_model(self):
        """Carga el modelo de TensorFlow"""
        try:
            logger.info(f"Cargando modelo desde: {self.model_path}")
            self.model = load_model(self.model_path)
            logger.info("Modelo cargado exitosamente")
        except Exception as e:
            logger.error(f"Error al cargar el modelo: {e}")
            raise
    
    async def predict_disease(self, image: Image.Image) -> PredictionResult:
        """
        Predice la enfermedad en una imagen de planta
        
        Args:
            image: Imagen de la planta a analizar
            
        Returns:
            PredictionResult con la predicción y confianza
        """
        try:
            # Preprocesar imagen
            processed_image = await self.preprocess_image(image)
            
            # Realizar predicción
            predictions = self.model.predict(processed_image)
            
            # Obtener clase predicha y confianza
            predicted_index = np.argmax(predictions[0])
            predicted_class = self.class_names[predicted_index]
            confidence = float(predictions[0][predicted_index])
            
            # Crear resultado de predicción
            prediction_result = PredictionResult(
                predicted_class=predicted_class,
                confidence=confidence,
                all_predictions=predictions[0].tolist()
            )
            
            logger.info(f"Predicción realizada: {predicted_class} con confianza {confidence}")
            return prediction_result
            
        except Exception as e:
            logger.error(f"Error al realizar predicción: {e}")
            raise
    
    async def preprocess_image(self, image: Image.Image, target_size: tuple = (480, 480)) -> np.ndarray:
        """
        Preprocesa una imagen para el modelo de predicción
        
        Args:
            image: Imagen a preprocesar
            target_size: Tamaño objetivo para redimensionar
            
        Returns:
            Array numpy preprocesado
        """
        try:
            # Redimensionar imagen
            resized_image = image.resize(target_size)
            
            # Convertir a array y normalizar
            img_array = image.img_to_array(resized_image)
            img_array = np.expand_dims(img_array, axis=0) / 255.0
            
            return img_array
            
        except Exception as e:
            logger.error(f"Error al preprocesar imagen: {e}")
            raise
    
    def get_supported_diseases(self) -> List[str]:
        """
        Obtiene la lista de enfermedades soportadas por el modelo
        
        Returns:
            Lista de nombres de enfermedades
        """
        return self.class_names.copy() 