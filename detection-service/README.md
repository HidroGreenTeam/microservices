# Detection Service - DDD + CQRS Architecture

Servicio de detección de enfermedades en plantas implementado con Domain-Driven Design (DDD) y Command Query Responsibility Segregation (CQRS).

## 🏗️ Arquitectura

Este servicio sigue los principios de **Domain-Driven Design (DDD)** y **CQRS** para proporcionar una arquitectura escalable y mantenible:

### Capas de la Arquitectura

```
src/
├── domain/                 # Capa de Dominio
│   ├── entities/          # Entidades de dominio
│   ├── value_objects/     # Objetos de valor
│   ├── repositories/      # Interfaces de repositorios
│   └── services/          # Servicios de dominio
├── application/           # Capa de Aplicación
│   ├── commands/          # Comandos (modificaciones)
│   ├── queries/           # Consultas (lecturas)
│   ├── handlers/          # Manejadores de comandos y consultas
│   └── dtos/              # Objetos de transferencia de datos
├── infrastructure/        # Capa de Infraestructura
│   ├── persistence/       # Implementaciones de persistencia
│   ├── services/          # Servicios externos
│   └── config/            # Configuraciones
└── interfaces/            # Capa de Interfaces
    └── rest/              # Controladores REST
```

### Principios DDD Implementados

1. **Entidades de Dominio**: `Diagnosis` con reglas de negocio encapsuladas
2. **Value Objects**: `PredictionResult` para representar resultados de predicción
3. **Repositorios**: Abstracción para acceso a datos
4. **Servicios de Dominio**: Lógica de negocio compleja
5. **Agregados**: Diagnóstico como agregado principal

### Principios CQRS Implementados

1. **Comandos**: Para modificar el estado del sistema
   - `CreateDiagnosisCommand`
   - `PredictDiseaseCommand`

2. **Consultas**: Para leer datos del sistema
   - `GetDiagnosisByIdQuery`
   - `GetDiagnosisByProfileQuery`
   - `GetDiagnosisByCropQuery`
   - `GetStatisticsQuery`

3. **Separación de Responsabilidades**: 
   - Handlers específicos para comandos y consultas
   - DTOs separados para entrada y salida

## 🚀 Características

- **Detección de Enfermedades**: Usando modelo de TensorFlow pre-entrenado
- **Almacenamiento de Imágenes**: Integración con Cloudinary
- **Persistencia**: Base de datos MySQL con SQLAlchemy
- **Mensajería**: RabbitMQ para notificaciones
- **Service Discovery**: Registro en Eureka
- **API REST**: Endpoints documentados con FastAPI

## 📋 Endpoints Disponibles

### Comandos (Modificaciones)
- `POST /api/v1/detections/predict` - Predicción sin guardar
- `POST /api/v1/detections/diagnose` - Diagnóstico completo

### Consultas (Lecturas)
- `GET /api/v1/detections/{farmerId}` - Historial por perfil
- `GET /api/v1/detections/detail/{diagnosisId}` - Diagnóstico por ID
- `GET /api/v1/detections/crop/{cropId}` - Diagnósticos por cultivo
- `GET /api/v1/detections/statistics` - Estadísticas del servicio

### Health Check
- `GET /api/v1/health` - Estado del servicio

## 🛠️ Tecnologías

- **FastAPI**: Framework web
- **TensorFlow**: Modelo de IA
- **SQLAlchemy**: ORM para persistencia
- **Cloudinary**: Almacenamiento de imágenes
- **RabbitMQ**: Mensajería
- **Eureka**: Service discovery
- **Poetry**: Gestión de dependencias

## 🔧 Configuración

### Variables de Entorno

```bash
DATABASE_URL=mysql+pymysql://root:root@localhost:3307/ayni
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
EUREKA_SERVER=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka
EUREKA_INSTANCE_HOSTNAME=detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### Instalación

```bash
# Instalar dependencias
poetry install

# Ejecutar el servicio
poetry run python -m src.main
```

## 🧪 Testing

```bash
# Ejecutar tests
poetry run pytest

# Formatear código
poetry run black src/

# Verificar tipos
poetry run mypy src/
```

## 📊 Modelo de Datos

### Entidad Diagnosis
- `id`: Identificador único
- `crop_id`: ID del cultivo
- `profile_id`: ID del perfil
- `predicted_class`: Clase predicha
- `confidence`: Nivel de confianza
- `disease_detected`: Si se detectó enfermedad
- `requires_treatment`: Si requiere tratamiento
- `image_url`: URL de la imagen
- `image_public_id`: ID público en Cloudinary
- `created_at`: Fecha de creación
- `updated_at`: Fecha de actualización

## 🔄 Flujo de Trabajo

1. **Recepción de Imagen**: El controlador recibe una imagen
2. **Validación**: Se validan los datos de entrada
3. **Predicción**: El servicio de dominio procesa la imagen
4. **Persistencia**: Se guarda en la base de datos
5. **Notificación**: Se envía mensaje si requiere tratamiento
6. **Respuesta**: Se retorna el resultado al cliente

## 🎯 Beneficios de la Arquitectura

- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad específica
- **Testabilidad**: Fácil de testear cada componente por separado
- **Escalabilidad**: Posibilidad de escalar comandos y consultas independientemente
- **Mantenibilidad**: Código organizado y fácil de entender
- **Flexibilidad**: Fácil cambio de implementaciones sin afectar el dominio

## 📈 Próximos Pasos

- [ ] Implementar Event Sourcing
- [ ] Agregar más validaciones de dominio
- [ ] Implementar cache para consultas frecuentes
- [ ] Agregar métricas y monitoreo
- [ ] Implementar versionado de API
