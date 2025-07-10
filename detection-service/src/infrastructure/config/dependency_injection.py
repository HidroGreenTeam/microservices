from typing import Generator
from sqlalchemy.orm import Session

from ..config.database_config import database_config
from ..persistence.sqlalchemy_diagnosis_repository import SQLAlchemyDiagnosisRepository
from ..services.tensorflow_disease_detection_service import TensorFlowDiseaseDetectionService
from ..services.cloudinary_image_upload_service import CloudinaryImageUploadService
from ..services.rabbitmq_messaging_service import RabbitMQMessagingService
from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from ...domain.services.disease_detection_service import DiseaseDetectionService
from ...infrastructure.services.image_upload_service import ImageUploadService
from ...infrastructure.services.messaging_service import MessagingService

class DependencyContainer:
    """Contenedor de inyección de dependencias"""
    
    def __init__(self):
        self._disease_detection_service = None
        self._image_upload_service = None
        self._messaging_service = None
    
    def get_database_session(self) -> Generator[Session, None, None]:
        """Obtiene una sesión de base de datos"""
        return database_config.get_session()
    
    def get_diagnosis_repository(self, db_session: Session) -> DiagnosisRepository:
        """Obtiene el repositorio de diagnósticos"""
        return SQLAlchemyDiagnosisRepository(db_session)
    
    def get_disease_detection_service(self) -> DiseaseDetectionService:
        """Obtiene el servicio de detección de enfermedades"""
        if self._disease_detection_service is None:
            self._disease_detection_service = TensorFlowDiseaseDetectionService()
        return self._disease_detection_service
    
    def get_image_upload_service(self) -> ImageUploadService:
        """Obtiene el servicio de subida de imágenes"""
        if self._image_upload_service is None:
            self._image_upload_service = CloudinaryImageUploadService()
        return self._image_upload_service
    
    def get_messaging_service(self) -> MessagingService:
        """Obtiene el servicio de mensajería"""
        if self._messaging_service is None:
            self._messaging_service = RabbitMQMessagingService()
        return self._messaging_service

# Instancia global del contenedor
dependency_container = DependencyContainer() 