import logging
from PIL import Image
import io

from ...domain.services.disease_detection_service import DiseaseDetectionService
from ..commands.predict_disease_command import PredictDiseaseCommand
from ..dtos.prediction_dto import PredictionDTO

logger = logging.getLogger(__name__)

class PredictDiseaseHandler:
    """Manejador para el comando PredictDiseaseCommand"""
    
    def __init__(self, disease_detection_service: DiseaseDetectionService):
        self.disease_detection_service = disease_detection_service
    
    async def handle(self, command: PredictDiseaseCommand) -> PredictionDTO:
        """
        Maneja el comando para predecir enfermedad sin guardar
        
        Args:
            command: Comando con la imagen a analizar
            
        Returns:
            PredictionDTO con el resultado de la predicción
        """
        try:
            # Leer y procesar la imagen
            contents = await command.file.read()
            image = Image.open(io.BytesIO(contents)).convert("RGB")
            
            # Realizar predicción
            prediction_result = await self.disease_detection_service.predict_disease(image)
            
            # Convertir a DTO
            prediction_dto = PredictionDTO.from_value_object(prediction_result)
            
            logger.info(f"Predicción realizada: {prediction_result.predicted_class} con confianza {prediction_result.confidence}")
            return prediction_dto
            
        except Exception as e:
            logger.error(f"Error al realizar predicción: {e}")
            raise 