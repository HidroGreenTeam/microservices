from typing import Optional
import logging
from PIL import Image
import io
from datetime import datetime

from ...domain.entities.diagnosis import Diagnosis
from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from ...domain.services.disease_detection_service import DiseaseDetectionService
from ...infrastructure.services.image_upload_service import ImageUploadService
from ...infrastructure.services.messaging_service import MessagingService
from ..commands.create_diagnosis_command import CreateDiagnosisCommand
from ..dtos.diagnosis_dto import DiagnosisDTO

logger = logging.getLogger(__name__)

class CreateDiagnosisHandler:
    """Manejador para el comando CreateDiagnosisCommand"""
    
    def __init__(
        self,
        diagnosis_repository: DiagnosisRepository,
        disease_detection_service: DiseaseDetectionService,
        image_upload_service: ImageUploadService,
        messaging_service: MessagingService
    ):
        self.diagnosis_repository = diagnosis_repository
        self.disease_detection_service = disease_detection_service
        self.image_upload_service = image_upload_service
        self.messaging_service = messaging_service
    
    async def handle(self, command: CreateDiagnosisCommand) -> DiagnosisDTO:
        """
        Maneja el comando para crear un nuevo diagnóstico
        
        Args:
            command: Comando con los datos necesarios
            
        Returns:
            DiagnosisDTO con el diagnóstico creado
        """
        try:
            # Leer y procesar la imagen
            contents = await command.file.read()
            image = Image.open(io.BytesIO(contents)).convert("RGB")
            
            # Realizar predicción
            prediction_result = await self.disease_detection_service.predict_disease(image)
            
            # Subir imagen a Cloudinary
            await command.file.seek(0)  # Resetear el puntero del archivo
            upload_result = await self.image_upload_service.upload_image(command.file, "hidrogreen/diagnosis")
            
            if not upload_result:
                raise Exception("Error al subir imagen a Cloudinary")
            
            # Crear entidad de diagnóstico
            diagnosis = Diagnosis(
                id=None,  # Se asignará al guardar
                crop_id=command.crop_id,
                profile_id=command.profile_id,
                predicted_class=prediction_result.predicted_class,
                confidence=prediction_result.confidence,
                disease_detected=prediction_result.disease_detected,
                requires_treatment=prediction_result.requires_treatment,
                image_url=upload_result["url"],
                image_public_id=upload_result["public_id"],
                created_at=datetime.utcnow(),
                updated_at=datetime.utcnow()
            )
            
            # Guardar en repositorio
            saved_diagnosis = await self.diagnosis_repository.save(diagnosis)
            
            # Convertir a DTO
            diagnosis_dto = DiagnosisDTO.from_entity(saved_diagnosis)
            
            # Si requiere tratamiento, enviar mensaje
            if saved_diagnosis.should_notify_treatment_service():
                success = self.messaging_service.send_diagnosis_message(diagnosis_dto.to_dict())
                if not success:
                    logger.warning("No se pudo enviar mensaje a RabbitMQ, pero el diagnóstico se completó")
            
            logger.info(f"Diagnóstico creado exitosamente: {saved_diagnosis.id}")
            return diagnosis_dto
            
        except Exception as e:
            logger.error(f"Error al crear diagnóstico: {e}")
            raise 