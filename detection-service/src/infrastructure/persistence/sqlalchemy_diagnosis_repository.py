import logging
from typing import List, Optional
from datetime import datetime
from sqlalchemy.orm import Session
from sqlalchemy import func

from ...domain.entities.diagnosis import Diagnosis
from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from .models import DiagnosisModel

logger = logging.getLogger(__name__)

class SQLAlchemyDiagnosisRepository(DiagnosisRepository):
    """Implementación del repositorio de Diagnosis usando SQLAlchemy"""
    
    def __init__(self, db_session: Session):
        self.db_session = db_session
    
    def _to_entity(self, model: DiagnosisModel) -> Diagnosis:
        """Convierte un modelo SQLAlchemy a entidad de dominio"""
        return Diagnosis(
            id=model.id,
            crop_id=model.crop_id,
            profile_id=model.profile_id,
            predicted_class=model.predicted_class,
            confidence=model.confidence,
            disease_detected=model.disease_detected,
            requires_treatment=model.requires_treatment,
            image_url=model.image_url,
            image_public_id=model.image_public_id,
            created_at=model.created_at,
            updated_at=model.updated_at
        )
    
    def _to_model(self, entity: Diagnosis) -> DiagnosisModel:
        """Convierte una entidad de dominio a modelo SQLAlchemy"""
        return DiagnosisModel(
            id=entity.id,
            crop_id=entity.crop_id,
            profile_id=entity.profile_id,
            predicted_class=entity.predicted_class,
            confidence=entity.confidence,
            disease_detected=entity.disease_detected,
            requires_treatment=entity.requires_treatment,
            image_url=entity.image_url,
            image_public_id=entity.image_public_id,
            created_at=entity.created_at,
            updated_at=entity.updated_at
        )
    
    async def save(self, diagnosis: Diagnosis) -> Diagnosis:
        """Guarda un diagnóstico en el repositorio"""
        try:
            model = self._to_model(diagnosis)
            self.db_session.add(model)
            self.db_session.commit()
            self.db_session.refresh(model)
            
            saved_entity = self._to_entity(model)
            logger.info(f"Diagnóstico guardado: {saved_entity.id}")
            return saved_entity
            
        except Exception as e:
            self.db_session.rollback()
            logger.error(f"Error al guardar diagnóstico: {e}")
            raise
    
    async def find_by_id(self, diagnosis_id: int) -> Optional[Diagnosis]:
        """Busca un diagnóstico por su ID"""
        try:
            model = self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.id == diagnosis_id
            ).first()
            
            if not model:
                return None
            
            return self._to_entity(model)
            
        except Exception as e:
            logger.error(f"Error al buscar diagnóstico por ID: {e}")
            raise
    
    async def find_by_profile_id(self, profile_id: int) -> List[Diagnosis]:
        """Busca todos los diagnósticos de un perfil específico"""
        try:
            models = self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.profile_id == profile_id
            ).order_by(DiagnosisModel.created_at.desc()).all()
            
            return [self._to_entity(model) for model in models]
            
        except Exception as e:
            logger.error(f"Error al buscar diagnósticos por perfil: {e}")
            raise
    
    async def find_by_crop_id(self, crop_id: int) -> List[Diagnosis]:
        """Busca todos los diagnósticos de un cultivo específico"""
        try:
            models = self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.crop_id == crop_id
            ).order_by(DiagnosisModel.created_at.desc()).all()
            
            return [self._to_entity(model) for model in models]
            
        except Exception as e:
            logger.error(f"Error al buscar diagnósticos por cultivo: {e}")
            raise
    
    async def find_all(self) -> List[Diagnosis]:
        """Obtiene todos los diagnósticos"""
        try:
            models = self.db_session.query(DiagnosisModel).order_by(
                DiagnosisModel.created_at.desc()
            ).all()
            
            return [self._to_entity(model) for model in models]
            
        except Exception as e:
            logger.error(f"Error al obtener todos los diagnósticos: {e}")
            raise
    
    async def find_diseases_detected(self) -> List[Diagnosis]:
        """Obtiene todos los diagnósticos donde se detectó enfermedad"""
        try:
            models = self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.disease_detected == True
            ).order_by(DiagnosisModel.created_at.desc()).all()
            
            return [self._to_entity(model) for model in models]
            
        except Exception as e:
            logger.error(f"Error al obtener diagnósticos con enfermedades: {e}")
            raise
    
    async def find_requiring_treatment(self) -> List[Diagnosis]:
        """Obtiene todos los diagnósticos que requieren tratamiento"""
        try:
            models = self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.requires_treatment == True
            ).order_by(DiagnosisModel.created_at.desc()).all()
            
            return [self._to_entity(model) for model in models]
            
        except Exception as e:
            logger.error(f"Error al obtener diagnósticos que requieren tratamiento: {e}")
            raise
    
    async def count_total_diagnoses(self) -> int:
        """Cuenta el total de diagnósticos"""
        try:
            return self.db_session.query(DiagnosisModel).count()
        except Exception as e:
            logger.error(f"Error al contar diagnósticos totales: {e}")
            raise
    
    async def count_diseases_detected(self) -> int:
        """Cuenta el total de diagnósticos con enfermedades detectadas"""
        try:
            return self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.disease_detected == True
            ).count()
        except Exception as e:
            logger.error(f"Error al contar enfermedades detectadas: {e}")
            raise
    
    async def count_requiring_treatment(self) -> int:
        """Cuenta el total de diagnósticos que requieren tratamiento"""
        try:
            return self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.requires_treatment == True
            ).count()
        except Exception as e:
            logger.error(f"Error al contar tratamientos requeridos: {e}")
            raise
    
    async def get_disease_statistics(self) -> dict:
        """Obtiene estadísticas de distribución de enfermedades"""
        try:
            # Obtener diagnósticos con enfermedades
            models = self.db_session.query(DiagnosisModel).filter(
                DiagnosisModel.disease_detected == True
            ).all()
            
            # Contar por tipo de enfermedad
            disease_stats = {}
            for model in models:
                disease = model.predicted_class
                if disease not in disease_stats:
                    disease_stats[disease] = 0
                disease_stats[disease] += 1
            
            return disease_stats
            
        except Exception as e:
            logger.error(f"Error al obtener estadísticas de enfermedades: {e}")
            raise 