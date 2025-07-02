from fastapi import FastAPI, UploadFile, File, HTTPException
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

load_dotenv()  # Carga las variables de entorno desde el archivo .env

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

MODEL_PATH = os.path.join("model", "model-8678.keras")
model = load_model(MODEL_PATH)

class_names = ['miner', 'nodisease', 'phoma', 'redspider', 'rust']

EUREKA_SERVER = os.getenv("EUREKA_SERVER", "https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka")
SERVICE_PORT = 8000
SERVICE_HOST = os.getenv("EUREKA_INSTANCE_HOSTNAME", "detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io")
SERVICE_SECURE_PORT = int(os.getenv("EUREKA_INSTANCE_SECURE_PORT", "443"))

@asynccontextmanager
async def lifespan(app: FastAPI):
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
        img = img.resize((300, 300))
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
    profile_id: int = None
):
    """
    Realiza diagnóstico completo y envía mensaje a RabbitMQ si se requiere tratamiento
    """
    try:
        if not crop_id or not profile_id:
            raise HTTPException(status_code=400, detail="crop_id y profile_id son requeridos")
        
        # Realizar predicción
        contents = await file.read()
        img = Image.open(io.BytesIO(contents)).convert("RGB")
        img = img.resize((300, 300))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0) / 255.0

        predictions = model.predict(img_array)
        predicted_index = np.argmax(predictions[0])
        predicted_class = class_names[predicted_index]
        confidence = float(predictions[0][predicted_index])
        
        # Determinar si se detectó enfermedad
        disease_detected = predicted_class != 'nodisease'
        requires_treatment = disease_detected and confidence > 0.90
        
        # Generar ID de diagnóstico (en producción usar UUID)
        import time
        diagnosis_id = int(time.time() * 1000)
        
        # Guardar imagen (en producción usar servicio de almacenamiento)
        image_url = f"/images/{diagnosis_id}_{file.filename}"
        
        result = {
            "diagnosis_id": diagnosis_id,
            "crop_id": crop_id,
            "profile_id": profile_id,
            "predicted_class": predicted_class,
            "confidence": confidence,
            "disease_detected": disease_detected,
            "requires_treatment": requires_treatment,
            "image_url": image_url
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

# Health check endpoint
@app.get("/api/v1/health")
async def health_check():
    return {"status": "UP", "service": "detection-service"}

app.include_router(router)
