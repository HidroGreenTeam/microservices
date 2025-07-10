from fastapi import FastAPI
from contextlib import asynccontextmanager
import logging
from dotenv import load_dotenv
import os

from py_eureka_client import eureka_client

from .infrastructure.config.database_config import database_config
from .infrastructure.config.dependency_injection import dependency_container
from .interfaces.rest.detection_controller import router as detection_router
from .interfaces.rest.health_controller import router as health_router

# Cargar variables de entorno
load_dotenv()

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configuración de Eureka
SERVICE_PORT = 8000

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Gestión del ciclo de vida de la aplicación"""
    try:
        # Crear tablas en la base de datos
        database_config.create_tables()
        logger.info("Base de datos inicializada")
        
        # Configuración básica de Eureka
        eureka_server = os.getenv("EUREKA_SERVER", "http://discovery-service:8761/eureka")
        service_host = os.getenv("EUREKA_INSTANCE_HOSTNAME", "detection-service")
        secure_port = int(os.getenv("EUREKA_INSTANCE_SECURE_PORT", "443"))
        
        # Registrar servicio en Eureka con configuración para Azure Container Apps
        await eureka_client.init_async(
            eureka_server=eureka_server,
            app_name="detection-service",
            instance_host=service_host,
            instance_port=secure_port,
            instance_secure_port_enabled=True,
            home_page_url=f"https://{service_host}/",
            status_page_url=f"https://{service_host}/api/v1/health",
            health_check_url=f"https://{service_host}/api/v1/health"
        )
        logger.info(f"Servicio registrado en Eureka con hostname: {service_host} y puerto seguro: {secure_port}")
        
        yield
        
    except Exception as e:
        logger.error(f"Error durante el inicio de la aplicación: {e}")
        raise
    finally:
        # Cerrar conexión con RabbitMQ al finalizar
        try:
            messaging_service = dependency_container.get_messaging_service()
            messaging_service.close_connection()
            logger.info("Conexión con RabbitMQ cerrada")
        except Exception as e:
            logger.warning(f"Error al cerrar conexión con RabbitMQ: {e}")

# Crear aplicación FastAPI
app = FastAPI(
    title="Plant Disease Detection API",
    description="API para detectar enfermedades en plantas usando un modelo pre-entrenado con arquitectura DDD y CQRS",
    version="2.0.0",
    lifespan=lifespan,
)

# Incluir routers
app.include_router(detection_router)
app.include_router(health_router)

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