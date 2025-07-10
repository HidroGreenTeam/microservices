from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
import logging

from ...infrastructure.config.dependency_injection import dependency_container

logger = logging.getLogger(__name__)

router = APIRouter()

def get_db():
    """Dependency para obtener sesión de base de datos"""
    return next(dependency_container.get_database_session())

@router.get("/api/v1/health")
async def health_check(db: Session = Depends(get_db)):
    """
    Health check del servicio
    """
    try:
        # Verificar conexión a la base de datos
        db.execute("SELECT 1")
        
        # Obtener estadísticas básicas
        diagnosis_repository = dependency_container.get_diagnosis_repository(db)
        total_diagnoses = await diagnosis_repository.count_total_diagnoses()
        diseases_detected = await diagnosis_repository.count_diseases_detected()
        
        return {
            "status": "UP", 
            "service": "detection-service",
            "database": "connected",
            "statistics": {
                "total_diagnoses": total_diagnoses,
                "diseases_detected": diseases_detected,
                "cloudinary": "configured"
            }
        }
    except Exception as e:
        return {
            "status": "DOWN", 
            "service": "detection-service",
            "database": "disconnected",
            "error": str(e)
        } 