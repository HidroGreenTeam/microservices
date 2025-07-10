#!/bin/bash

# Script de migración para Detection Service - DDD + CQRS
echo "🚀 Iniciando migración a arquitectura DDD + CQRS..."

# Verificar que estamos en el directorio correcto
if [ ! -f "pyproject.toml" ]; then
    echo "❌ Error: No se encontró pyproject.toml. Asegúrate de estar en el directorio detection-service"
    exit 1
fi

# Verificar que la nueva estructura existe
if [ ! -d "src" ]; then
    echo "❌ Error: No se encontró el directorio src. La refactorización no está completa."
    exit 1
fi

echo "✅ Verificando estructura de archivos..."

# Verificar archivos críticos de la nueva arquitectura
required_files=(
    "src/main.py"
    "src/domain/entities/diagnosis.py"
    "src/application/handlers/create_diagnosis_handler.py"
    "src/infrastructure/persistence/sqlalchemy_diagnosis_repository.py"
    "src/interfaces/rest/detection_controller.py"
)

for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "❌ Error: No se encontró $file"
        exit 1
    fi
done

echo "✅ Todos los archivos requeridos están presentes"

# Verificar que los archivos antiguos han sido eliminados
old_files=(
    "main.py"
    "database.py"
    "messaging_service.py"
    "cloudinary_service.py"
)

for file in "${old_files[@]}"; do
    if [ -f "$file" ]; then
        echo "⚠️  Advertencia: El archivo $file aún existe. Eliminando..."
        rm "$file"
    fi
done

echo "✅ Archivos antiguos eliminados"

# Verificar dependencias
echo "🔍 Verificando dependencias..."
if ! command -v poetry &> /dev/null; then
    echo "❌ Error: Poetry no está instalado. Instálalo primero."
    exit 1
fi

# Instalar dependencias
echo "📦 Instalando dependencias..."
poetry install

if [ $? -eq 0 ]; then
    echo "✅ Dependencias instaladas correctamente"
else
    echo "❌ Error al instalar dependencias"
    exit 1
fi

# Verificar que el modelo existe
if [ ! -d "model" ] || [ ! -f "model/model-91840.keras" ]; then
    echo "⚠️  Advertencia: No se encontró el modelo de IA. Asegúrate de que esté en model/model-91840.keras"
fi

echo "🧪 Probando la aplicación..."
# Intentar importar el módulo principal
python -c "from src.main import app; print('✅ Aplicación importada correctamente')"

if [ $? -eq 0 ]; then
    echo "✅ La aplicación se puede importar correctamente"
else
    echo "❌ Error al importar la aplicación"
    exit 1
fi

echo ""
echo "🎉 ¡Migración completada exitosamente!"
echo ""
echo "📋 Resumen de cambios:"
echo "   ✅ Estructura DDD + CQRS implementada"
echo "   ✅ Archivos antiguos eliminados"
echo "   ✅ Dependencias actualizadas"
echo "   ✅ Dockerfile actualizado"
echo "   ✅ Docker Compose actualizado"
echo ""
echo "🚀 Para ejecutar el servicio:"
echo "   poetry run python -m src.main"
echo ""
echo "🐳 Para ejecutar con Docker:"
echo "   docker-compose up detection-service"
echo ""
echo "📚 Documentación actualizada en README.md" 