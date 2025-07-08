from fastapi import FastAPI, UploadFile, File, HTTPException, Depends
from fastapi.responses import JSONResponse
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
import numpy as np
from PIL import Image
import io
import os
from py_eureka_client import eureka_client
import asyncio
from contextlib import asynccontextmanager
from fastapi import APIRouter
from dotenv import load_dotenv
import logging
from messaging_service import messaging_service
from cloudinary_service import cloudinary_service
from database import get_db, Diagnosis, create_tables
from sqlalchemy.orm import Session
from typing import List, Optional
import time

load_dotenv()  # Carga las variables de entorno desde el archivo .env

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

MODEL_PATH = os.path.join("model", "model-91840.keras")
model = load_model(MODEL_PATH)

class_names = ['miner', 'nodisease', 'phoma', 'redspider', 'rust']

EUREKA_SERVER = os.getenv("EUREKA_SERVER", "https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka")
SERVICE_PORT = 8000
SERVICE_HOST = os.getenv("EUREKA_INSTANCE_HOSTNAME", "detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io")
SERVICE_SECURE_PORT = int(os.getenv("EUREKA_INSTANCE_SECURE_PORT", "443"))

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Crear tablas en la base de datos
    create_tables()
    
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name="detection-service",
        instance_port=SERVICE_SECURE_PORT,
        instance_host=SERVICE_HOST,
        instance_secure_port_enabled=True,
        home_page_url=f"https://{SERVICE_HOST}/",
        status_page_url=f"https://{SERVICE_HOST}/api/v1/health",
        health_check_url=f"https://{SERVICE_HOST}/api/v1/health",
    )
    yield
    # Cerrar conexión con RabbitMQ al finalizar
    messaging_service.close_connection()

app = FastAPI(
    title="Plant Disease Detection API",
    description="An API for detecting plant diseases using a pre-trained model.",
    version="1.0.0",
    prefix="/api/v1",
    lifespan=lifespan,
)

router = APIRouter(prefix="/api/v1/detections")

@router.post("/predict")
async def predict(file: UploadFile = File(...)):
    try:
        contents = await file.read()
        img = Image.open(io.BytesIO(contents)).convert("RGB")
        img = img.resize((224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0) / 255.0

        predictions = model.predict(img_array)
        predicted_index = np.argmax(predictions[0])
        predicted_class = class_names[predicted_index]
        confidence = float(predictions[0][predicted_index])
        
        # Determinar si se detectó enfermedad
        disease_detected = predicted_class != 'nodisease'
        requires_treatment = disease_detected and confidence > 0.90

        result = {
            "predicted_class": predicted_class,
            "confidence": confidence,
            "disease_detected": disease_detected,
            "requires_treatment": requires_treatment
        }
        
        return result
    except Exception as e:
        logger.error(f"Error en predicción: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/diagnose")
async def diagnose(
    file: UploadFile = File(...),
    crop_id: int = None,
    profile_id: int = None,
    db: Session = Depends(get_db)
):
    """
    Realiza diagnóstico completo, guarda imagen en Cloudinary y almacena en base de datos
    """
    try:
        if not crop_id or not profile_id:
            raise HTTPException(status_code=400, detail="crop_id y profile_id son requeridos")
        
        # Subir imagen a Cloudinary
        cloudinary_result = await cloudinary_service.upload_image(file, "hidrogreen/diagnosis")
        if not cloudinary_result:
            raise HTTPException(status_code=500, detail="Error al subir imagen a Cloudinary")
        
        # Realizar predicción
        await file.seek(0)  # Resetear el puntero del archivo
        contents = await file.read()
        img = Image.open(io.BytesIO(contents)).convert("RGB")
        img = img.resize((224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0) / 255.0

        predictions = model.predict(img_array)
        predicted_index = np.argmax(predictions[0])
        predicted_class = class_names[predicted_index]
        confidence = float(predictions[0][predicted_index])
        
        # Determinar si se detectó enfermedad
        disease_detected = predicted_class != 'nodisease'
        requires_treatment = disease_detected and confidence > 0.90
        
        # Guardar diagnóstico en la base de datos
        diagnosis = Diagnosis(
            crop_id=crop_id,
            profile_id=profile_id,
            predicted_class=predicted_class,
            confidence=confidence,
            disease_detected=disease_detected,
            requires_treatment=requires_treatment,
            image_url=cloudinary_result["url"],
            image_public_id=cloudinary_result["public_id"]
        )
        
        db.add(diagnosis)
        db.commit()
        db.refresh(diagnosis)
        
        result = {
            "diagnosis_id": diagnosis.id,
            "crop_id": crop_id,
            "profile_id": profile_id,
            "predicted_class": predicted_class,
            "confidence": confidence,
            "disease_detected": disease_detected,
            "requires_treatment": requires_treatment,
            "image_url": cloudinary_result["url"],
            "image_public_id": cloudinary_result["public_id"],
            "created_at": diagnosis.created_at.isoformat()
        }
        
        # Si requiere tratamiento, enviar mensaje a RabbitMQ
        if requires_treatment:
            success = messaging_service.send_diagnosis_message(result)
            if not success:
                logger.warning("No se pudo enviar mensaje a RabbitMQ, pero el diagnóstico se completó")
        
        return result
        
    except Exception as e:
        logger.error(f"Error en diagnóstico: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/")
async def read_root():
    return {"Hello": "World"}

@router.get("/{farmerId}")
async def get_farmer_diagnosis_history(farmerId: int, db: Session = Depends(get_db)):
    """
    Obtiene el historial de diagnósticos de un farmer/profile
    """
    try:
        diagnoses = db.query(Diagnosis).filter(Diagnosis.profile_id == farmerId).order_by(Diagnosis.created_at.desc()).all()
        
        result = []
        for diagnosis in diagnoses:
            result.append({
                "diagnosis_id": diagnosis.id,
                "crop_id": diagnosis.crop_id,
                "profile_id": diagnosis.profile_id,
                "predicted_class": diagnosis.predicted_class,
                "confidence": diagnosis.confidence,
                "disease_detected": diagnosis.disease_detected,
                "requires_treatment": diagnosis.requires_treatment,
                "image_url": diagnosis.image_url,
                "created_at": diagnosis.created_at.isoformat(),
                "updated_at": diagnosis.updated_at.isoformat()
            })
        
        return result
        
    except Exception as e:
        logger.error(f"Error al obtener historial de diagnósticos: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/detail/{diagnosisId}")
async def get_diagnosis_by_id(diagnosisId: int, db: Session = Depends(get_db)):
    """
    Obtiene un diagnóstico específico por ID
    """
    try:
        diagnosis = db.query(Diagnosis).filter(Diagnosis.id == diagnosisId).first()
        
        if not diagnosis:
            raise HTTPException(status_code=404, detail="Diagnóstico no encontrado")
        
        result = {
            "diagnosis_id": diagnosis.id,
            "crop_id": diagnosis.crop_id,
            "profile_id": diagnosis.profile_id,
            "predicted_class": diagnosis.predicted_class,
            "confidence": diagnosis.confidence,
            "disease_detected": diagnosis.disease_detected,
            "requires_treatment": diagnosis.requires_treatment,
            "image_url": diagnosis.image_url,
            "image_public_id": diagnosis.image_public_id,
            "created_at": diagnosis.created_at.isoformat(),
            "updated_at": diagnosis.updated_at.isoformat()
        }
        
        return result
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error al obtener diagnóstico: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/crop/{cropId}")
async def get_crop_diagnosis(cropId: int, db: Session = Depends(get_db)):
    """
    Obtiene todos los diagnósticos de un crop específico
    """
    try:
        diagnoses = db.query(Diagnosis).filter(Diagnosis.crop_id == cropId).order_by(Diagnosis.created_at.desc()).all()
        
        result = []
        for diagnosis in diagnoses:
            result.append({
                "diagnosis_id": diagnosis.id,
                "crop_id": diagnosis.crop_id,
                "profile_id": diagnosis.profile_id,
                "predicted_class": diagnosis.predicted_class,
                "confidence": diagnosis.confidence,
                "disease_detected": diagnosis.disease_detected,
                "requires_treatment": diagnosis.requires_treatment,
                "image_url": diagnosis.image_url,
                "created_at": diagnosis.created_at.isoformat(),
                "updated_at": diagnosis.updated_at.isoformat()
            })
        
        return result
        
    except Exception as e:
        logger.error(f"Error al obtener diagnósticos del crop: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Health check endpoint
@app.get("/api/v1/health")
async def health_check(db: Session = Depends(get_db)):
    try:
        # Verificar conexión a la base de datos
        db.execute("SELECT 1")
        
        # Obtener estadísticas básicas
        total_diagnoses = db.query(Diagnosis).count()
        diseases_detected = db.query(Diagnosis).filter(Diagnosis.disease_detected == True).count()
        
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

@app.get("/api/v1/statistics")
async def get_statistics(db: Session = Depends(get_db)):
    """
    Obtiene estadísticas del servicio de detección
    """
    try:
        total_diagnoses = db.query(Diagnosis).count()
        diseases_detected = db.query(Diagnosis).filter(Diagnosis.disease_detected == True).count()
        treatments_required = db.query(Diagnosis).filter(Diagnosis.requires_treatment == True).count()
        
        # Estadísticas por tipo de enfermedad
        disease_stats = {}
        diagnoses = db.query(Diagnosis).filter(Diagnosis.disease_detected == True).all()
        for diagnosis in diagnoses:
            disease = diagnosis.predicted_class
            if disease not in disease_stats:
                disease_stats[disease] = 0
            disease_stats[disease] += 1
        
        return {
            "total_diagnoses": total_diagnoses,
            "diseases_detected": diseases_detected,
            "treatments_required": treatments_required,
            "healthy_crops": total_diagnoses - diseases_detected,
            "disease_distribution": disease_stats
        }
    except Exception as e:
        logger.error(f"Error al obtener estadísticas: {e}")
        raise HTTPException(status_code=500, detail=str(e))

app.include_router(router)
