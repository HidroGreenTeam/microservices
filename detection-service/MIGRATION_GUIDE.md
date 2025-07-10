# Guía de Migración - Detection Service DDD + CQRS

Esta guía te ayudará a migrar desde la versión anterior del Detection Service a la nueva arquitectura DDD + CQRS.

## 🎯 Cambios Principales

### Estructura de Archivos

**Antes:**
```
detection-service/
├── main.py
├── database.py
├── messaging_service.py
├── cloudinary_service.py
└── ...
```

**Después:**
```
detection-service/
├── src/
│   ├── domain/
│   │   ├── entities/
│   │   ├── value_objects/
│   │   ├── repositories/
│   │   └── services/
│   ├── application/
│   │   ├── commands/
│   │   ├── queries/
│   │   ├── handlers/
│   │   └── dtos/
│   ├── infrastructure/
│   │   ├── persistence/
│   │   ├── services/
│   │   └── config/
│   ├── interfaces/
│   │   └── rest/
│   └── main.py
└── ...
```

## 🚀 Migración Automática

### Opción 1: Script de Migración (Recomendado)

```bash
# Dar permisos de ejecución
chmod +x migrate_to_ddd.sh

# Ejecutar migración
./migrate_to_ddd.sh
```

### Opción 2: Migración Manual

#### Paso 1: Verificar la Nueva Estructura
```bash
# Verificar que todos los archivos están presentes
ls -la src/
ls -la src/domain/
ls -la src/application/
ls -la src/infrastructure/
ls -la src/interfaces/
```

#### Paso 2: Eliminar Archivos Antiguos
```bash
# Eliminar archivos de la versión anterior
rm -f main.py database.py messaging_service.py cloudinary_service.py
```

#### Paso 3: Instalar Dependencias
```bash
# Instalar dependencias actualizadas
poetry install
```

#### Paso 4: Verificar la Aplicación
```bash
# Probar que la aplicación se puede importar
python -c "from src.main import app; print('✅ OK')"
```

## 🔧 Configuración

### Variables de Entorno

Copia el archivo de ejemplo y ajusta los valores:

```bash
cp env.example .env
```

Variables importantes:
- `DATABASE_URL`: URL de conexión a MySQL
- `RABBITMQ_HOST`: Host de RabbitMQ
- `EUREKA_SERVER`: URL del servidor Eureka
- `USER_SERVICE_URL`: URL del servicio de usuarios

### Docker

El Dockerfile y docker-compose.yml ya están actualizados para la nueva arquitectura.

## 🧪 Testing

### Ejecutar Localmente
```bash
# Con Poetry
poetry run python -m src.main

# Con Python directo
python -m src.main
```

### Ejecutar con Docker
```bash
# Solo el detection-service
docker-compose up detection-service

# Todo el stack
docker-compose up
```

## 📋 Endpoints Verificados

Los siguientes endpoints mantienen la misma funcionalidad:

- `POST /api/v1/detections/predict` - Predicción sin guardar
- `POST /api/v1/detections/diagnose` - Diagnóstico completo
- `GET /api/v1/detections/{farmerId}` - Historial por perfil
- `GET /api/v1/detections/detail/{diagnosisId}` - Diagnóstico por ID
- `GET /api/v1/detections/crop/{cropId}` - Diagnósticos por cultivo
- `GET /api/v1/detections/statistics` - Estadísticas
- `GET /api/v1/health` - Health check

## 🔍 Verificación

### Health Check
```bash
curl http://localhost:8000/api/v1/health
```

### Predicción Simple
```bash
curl -X POST "http://localhost:8000/api/v1/detections/predict" \
  -H "accept: application/json" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@ruta/a/tu/imagen.jpg"
```

## ⚠️ Posibles Problemas

### Error: Módulo no encontrado
```bash
# Asegúrate de estar en el directorio correcto
cd detection-service

# Verifica que PYTHONPATH esté configurado
export PYTHONPATH=/app
```

### Error: Dependencias faltantes
```bash
# Reinstalar dependencias
poetry install --sync
```

### Error: Modelo no encontrado
```bash
# Verifica que el modelo esté en la ubicación correcta
ls -la model/model-91840.keras
```

## 📞 Soporte

Si encuentras problemas durante la migración:

1. Verifica que todos los archivos de la nueva estructura estén presentes
2. Revisa los logs de la aplicación
3. Asegúrate de que las variables de entorno estén configuradas correctamente
4. Verifica que las dependencias estén instaladas

## 🎉 Beneficios de la Nueva Arquitectura

- **Mantenibilidad**: Código organizado por responsabilidades
- **Testabilidad**: Fácil testing de componentes individuales
- **Escalabilidad**: Separación de comandos y consultas
- **Flexibilidad**: Fácil cambio de implementaciones
- **Documentación**: Código autodocumentado con DDD 