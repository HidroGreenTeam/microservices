from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from typing import List, Optional
import logging

from ...application.commands.create_diagnosis_command import CreateDiagnosisCommand
from ...application.commands.predict_disease_command import PredictDiseaseCommand
from ...application.queries.get_diagnosis_by_id_query import GetDiagnosisByIdQuery
from ...application.queries.get_diagnosis_by_profile_query import GetDiagnosisByProfileQuery
from ...application.queries.get_diagnosis_by_crop_query import GetDiagnosisByCropQuery
from ...application.queries.get_statistics_query import GetStatisticsQuery

from ...application.handlers.create_diagnosis_handler import CreateDiagnosisHandler
from ...application.handlers.predict_disease_handler import PredictDiseaseHandler
from ...application.handlers.get_diagnosis_by_id_handler import GetDiagnosisByIdHandler
from ...application.handlers.get_diagnosis_by_profile_handler import GetDiagnosisByProfileHandler
from ...application.handlers.get_diagnosis_by_crop_handler import GetDiagnosisByCropHandler
from ...application.handlers.get_statistics_handler import GetStatisticsHandler

from ...application.dtos.diagnosis_dto import DiagnosisDTO
from ...application.dtos.prediction_dto import PredictionDTO
from ...application.dtos.statistics_dto import StatisticsDTO

from ...infrastructure.config.dependency_injection import dependency_container

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/v1/detections")

def get_db():
    """Dependency para obtener sesión de base de datos"""
    return next(dependency_container.get_database_session())

@router.post("/predict")
async def predict_disease(file: UploadFile = File(...)):
    """
    Predice enfermedad en una imagen sin guardar en base de datos
    """
    try:
        # Crear comando
        command = PredictDiseaseCommand(file=file)
        
        # Obtener servicios
        disease_detection_service = dependency_container.get_disease_detection_service()
        
        # Crear y ejecutar handler
        handler = PredictDiseaseHandler(disease_detection_service)
        result = await handler.handle(command)
        
        return result.to_dict()
        
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Error en predicción: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/diagnose")
async def create_diagnosis(
    file: UploadFile = File(...),
    crop_id: int = None,
    profile_id: int = None,
    db: Session = Depends(get_db)
):
    """
    Realiza diagnóstico completo, guarda imagen en Cloudinary y almacena en base de datos
    """
    try:
        # Crear comando
        command = CreateDiagnosisCommand(
            file=file,
            crop_id=crop_id,
            profile_id=profile_id
        )
        
        # Obtener servicios
        diagnosis_repository = dependency_container.get_diagnosis_repository(db)
        disease_detection_service = dependency_container.get_disease_detection_service()
        image_upload_service = dependency_container.get_image_upload_service()
        messaging_service = dependency_container.get_messaging_service()
        
        # Crear y ejecutar handler
        handler = CreateDiagnosisHandler(
            diagnosis_repository=diagnosis_repository,
            disease_detection_service=disease_detection_service,
            image_upload_service=image_upload_service,
            messaging_service=messaging_service
        )
        
        result = await handler.handle(command)
        return result.to_dict()
        
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Error en diagnóstico: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/{farmerId}")
async def get_farmer_diagnosis_history(farmerId: int, db: Session = Depends(get_db)):
    """
    Obtiene el historial de diagnósticos de un farmer/profile
    """
    try:
        # Crear consulta
        query = GetDiagnosisByProfileQuery(profile_id=farmerId)
        
        # Obtener repositorio
        diagnosis_repository = dependency_container.get_diagnosis_repository(db)
        
        # Crear y ejecutar handler
        handler = GetDiagnosisByProfileHandler(diagnosis_repository)
        results = await handler.handle(query)
        
        return [result.to_dict() for result in results]
        
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Error al obtener historial de diagnósticos: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/detail/{diagnosisId}")
async def get_diagnosis_by_id(diagnosisId: int, db: Session = Depends(get_db)):
    """
    Obtiene un diagnóstico específico por ID
    """
    try:
        # Crear consulta
        query = GetDiagnosisByIdQuery(diagnosis_id=diagnosisId)
        
        # Obtener repositorio
        diagnosis_repository = dependency_container.get_diagnosis_repository(db)
        
        # Crear y ejecutar handler
        handler = GetDiagnosisByIdHandler(diagnosis_repository)
        result = await handler.handle(query)
        
        if not result:
            raise HTTPException(status_code=404, detail="Diagnóstico no encontrado")
        
        return result.to_dict()
        
    except HTTPException:
        raise
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Error al obtener diagnóstico: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/crop/{cropId}")
async def get_crop_diagnosis(cropId: int, db: Session = Depends(get_db)):
    """
    Obtiene todos los diagnósticos de un crop específico
    """
    try:
        # Crear consulta
        query = GetDiagnosisByCropQuery(crop_id=cropId)
        
        # Obtener repositorio
        diagnosis_repository = dependency_container.get_diagnosis_repository(db)
        
        # Crear y ejecutar handler
        handler = GetDiagnosisByCropHandler(diagnosis_repository)
        results = await handler.handle(query)
        
        return [result.to_dict() for result in results]
        
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Error al obtener diagnósticos del crop: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/statistics")
async def get_statistics(db: Session = Depends(get_db)):
    """
    Obtiene estadísticas del servicio de detección
    """
    try:
        # Crear consulta
        query = GetStatisticsQuery()
        
        # Obtener repositorio
        diagnosis_repository = dependency_container.get_diagnosis_repository(db)
        
        # Crear y ejecutar handler
        handler = GetStatisticsHandler(diagnosis_repository)
        result = await handler.handle(query)
        
        return result.to_dict()
        
    except Exception as e:
        logger.error(f"Error al obtener estadísticas: {e}")
        raise HTTPException(status_code=500, detail=str(e)) 