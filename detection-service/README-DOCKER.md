# 🐳 Detection Service - Configuración Docker

## 📋 Resumen

El **Detection Service** es un microservicio Python que utiliza FastAPI y TensorFlow para detectar enfermedades en plantas. Está configurado para funcionar perfectamente con Docker y Docker Compose.

## 🚀 Configuración Actual

### ✅ **CONFIGURACIÓN PARA DOCKER (DESARROLLO LOCAL)**

El servicio está configurado para funcionar automáticamente con Docker Compose:

```python
# Configuración para Docker (desarrollo local)
EUREKA_SERVER = "http://discovery-service:8761/eureka"
SERVICE_HOST = "detection-service"
SERVICE_PORT = 8000
```

### 🔧 **Variables de Entorno**

Las siguientes variables están configuradas en `docker-compose.yml`:

```yaml
environment:
  - EUREKA_SERVER=http://discovery-service:8761/eureka/
  - SERVICE_HOST=detection-service
  - RABBITMQ_HOST=rabbitmq
  - RABBITMQ_PORT=5672
  - RABBITMQ_USER=guest
  - RABBITMQ_PASSWORD=guest
  - USER_SERVICE_URL=http://user-service:8081
```

## 🏗️ **Arquitectura del Servicio**

```
Detection Service
├── FastAPI Application (Puerto 8000)
├── TensorFlow Model (model-8678.keras)
├── Eureka Client (Registro de servicios)
├── RabbitMQ Client (Mensajería)
└── Health Check Endpoint
```

## 📦 **Dependencias**

- **FastAPI**: Framework web
- **TensorFlow**: Modelo de ML
- **Pillow**: Procesamiento de imágenes
- **py-eureka-client**: Registro con Eureka
- **pika**: Cliente RabbitMQ
- **python-dotenv**: Variables de entorno

## 🎯 **Endpoints Disponibles**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/detections/predict` | POST | Predicción simple de enfermedad |
| `/api/v1/detections/diagnose` | POST | Diagnóstico completo con RabbitMQ |
| `/api/v1/detections/` | GET | Health check básico |
| `/api/v1/health` | GET | Health check detallado |
| `/docs` | GET | Documentación Swagger |

## 🐳 **Uso con Docker Compose**

### 1. **Ejecutar todo el stack**
```bash
# Desde la raíz del proyecto
docker-compose up -d
```

### 2. **Verificar el servicio**
```bash
# Health check
curl http://localhost:8000/api/v1/health

# Documentación
open http://localhost:8000/docs
```

### 3. **Logs del servicio**
```bash
docker-compose logs detection-service
```

## 🔄 **Flujo de Trabajo**

1. **Inicio**: El servicio se registra en Eureka
2. **Conexión**: Se conecta a RabbitMQ
3. **Modelo**: Carga el modelo TensorFlow
4. **API**: Expone endpoints para predicción
5. **Procesamiento**: Procesa imágenes y detecta enfermedades
6. **Notificación**: Envía mensajes a RabbitMQ si requiere tratamiento

## 🛠️ **Desarrollo Local**

### Requisitos
- Docker y Docker Compose
- Python 3.12+ (para desarrollo local)
- Poetry (para gestión de dependencias)

### Configuración Local (sin Docker)
```bash
# Instalar dependencias
poetry install

# NOTA: No se requiere archivo .env para Docker
# Las variables de entorno se configuran en docker-compose.yml

# Para desarrollo local sin Docker, configurar variables de entorno:
export EUREKA_SERVER="http://localhost:8761/eureka/"
export RABBITMQ_HOST="localhost"
export RABBITMQ_PORT="5672"
export RABBITMQ_USER="guest"
export RABBITMQ_PASSWORD="guest"

# Ejecutar el servicio
poetry run fastapi dev main.py
```

## 📊 **Monitoreo**

### Health Checks
```bash
# Basic health check
curl http://localhost:8000/api/v1/detections/

# Detailed health check
curl http://localhost:8000/api/v1/health
```

### Métricas
- **Puerto**: 8000
- **Registro Eureka**: Automático
- **RabbitMQ**: Cola `diagnosis_queue`
- **Modelo**: `model/model-8678.keras`

## ⚠️ **Configuración de Producción (Comentada)**

La configuración para Azure está comentada en el código:

```python
# Configuración comentada para producción (Azure)
# EUREKA_SERVER = "https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka"
# SERVICE_HOST = "detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io"
# SERVICE_SECURE_PORT = 443
```

## 🔧 **Troubleshooting**

### Problemas Comunes

1. **Error de conexión a Eureka**
   ```bash
   # Verificar que discovery-service esté ejecutándose
   docker-compose logs discovery-service
   ```

2. **Error de conexión a RabbitMQ**
   ```bash
   # Verificar RabbitMQ
   docker-compose logs rabbitmq
   ```

3. **Modelo no encontrado**
   ```bash
   # Verificar que el modelo existe
   ls -la detection-service/model/
   ```

## 🎨 **Ejemplo de Uso**

```bash
# Predicción simple
curl -X POST "http://localhost:8000/api/v1/detections/predict" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@imagen_planta.jpg"

# Diagnóstico completo
curl -X POST "http://localhost:8000/api/v1/detections/diagnose?crop_id=1&profile_id=1" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@imagen_planta.jpg"
```

## 📚 **Documentación Adicional**

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **OpenAPI JSON**: http://localhost:8000/openapi.json

---

✅ **El servicio está listo para usar con Docker Compose sin configuración adicional!** 