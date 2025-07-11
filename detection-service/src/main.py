from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging
from dotenv import load_dotenv
import os

import ssl
import certifi
import aiohttp

from py_eureka_client import eureka_client

from .infrastructure.config.database_config import database_config
from .infrastructure.config.dependency_injection import dependency_container
from .interfaces.rest.detection_controller import router as detection_router
from .interfaces.rest.health_controller import router as health_router

load_dotenv()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

SERVICE_PORT = 8000

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Gestión del ciclo de vida de la aplicación"""
    try:
        database_config.create_tables()
        logger.info("Base de datos inicializada")
        
        eureka_server = os.getenv("EUREKA_SERVER", "http://discovery-service:8761/eureka")
        service_hostname = os.getenv("EUREKA_INSTANCE_HOSTNAME", "detection-service")

        init_params = {
            "eureka_server": eureka_server,
            "app_name": "detection-service",
            "instance_host": service_hostname,
            "instance_port": SERVICE_PORT,
            "metadata": {
                "management.port": str(SERVICE_PORT)
            },
            "health_check_url": f"http://{service_hostname}:{SERVICE_PORT}/api/v1/health"
        }

        if eureka_server.startswith("https://"):
            if not eureka_server.endswith("/eureka/"):
                if eureka_server.endswith("/eureka"):
                    eureka_server = eureka_server + "/"
                elif not eureka_server.endswith("/"):
                    eureka_server = eureka_server + "/eureka/"
                else:
                    eureka_server = eureka_server + "eureka/"
            logger.info(f"Usando conexión segura a Eureka: {eureka_server}")
            
            init_params.update({
                "eureka_protocol": "https",
                "instance_secure_port": int(os.getenv("EUREKA_INSTANCE_SECURE_PORT", "443")),
                "instance_secure_port_enabled": True,
                "home_page_url": f"https://{service_hostname}/",
                "status_page_url": f"https://{service_hostname}/api/v1/health",
                "health_check_url": f"https://{service_hostname}/api/v1/health"
            })
        
        await eureka_client.init_async(**init_params)
        logger.info(f"Servicio registrado en Eureka con hostname: {service_hostname}")
        
        yield
        
    except Exception as e:
        logger.error(f"Error durante el inicio de la aplicación: {e}")
        raise
    finally:
        try:
            messaging_service = dependency_container.get_messaging_service()
            messaging_service.close_connection()
            logger.info("Conexión con RabbitMQ cerrada")
        except Exception as e:
            logger.warning(f"Error al cerrar conexión con RabbitMQ: {e}")

app = FastAPI(
    title="Plant Disease Detection API",
    description="API para detectar enfermedades en plantas usando un modelo pre-entrenado con arquitectura DDD y CQRS",
    version="2.0.0",
    lifespan=lifespan,
)

# Configuración de CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permite todos los orígenes
    allow_credentials=True,
    allow_methods=["*"],  # Permite todos los métodos
    allow_headers=["*"],  # Permite todas las cabeceras
)

app.include_router(detection_router, prefix="/api/v1/detections")
app.include_router(health_router, prefix="/api/v1/detections/health")

@app.get("/")
async def read_root():
    """Endpoint raíz"""
    return {
        "message": "Plant Disease Detection API",
        "version": "2.0.0",
        "architecture": "DDD + CQRS",
        "status": "running"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=SERVICE_PORT)