from fastapi import FastAPI, UploadFile, File
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

app = FastAPI(
    title="Plant Disease Detection API",
    description="An API for detecting plant diseases using a pre-trained model.",
    version="1.0.0"
)

MODEL_PATH = os.path.join("model", "model-8678.keras")
model = load_model(MODEL_PATH)

class_names = ['miner', 'nodisease', 'phoma', 'redspider', 'rust']

EUREKA_SERVER = "http://localhost:8761/eureka/"
SERVICE_PORT = 8000  # Cambia si usas otro puerto

@asynccontextmanager
async def lifespan(app: FastAPI):
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name="detection-service",
        instance_port=SERVICE_PORT,
        instance_host="localhost",
    )
    yield

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

        return {
            "predicted_class": predicted_class,
            "confidence": confidence
        }
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})

@router.get("/")
async def read_root():
    return {"Hello": "World"}

app.include_router(router)
