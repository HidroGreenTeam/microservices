# Usar imagen base de Python 3.12
FROM python:3.12-slim

# Establecer directorio de trabajo
WORKDIR /app

# Instalar dependencias del sistema
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    libc6-dev \
    libffi-dev \
    libssl-dev \
    default-libmysqlclient-dev \
    pkg-config \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar archivos de configuración
COPY pyproject.toml poetry.lock ./

# Instalar Poetry
RUN pip install poetry

# Configurar Poetry para no crear entorno virtual
RUN poetry config virtualenvs.create false

# Instalar dependencias
RUN poetry install --only=main

# Copiar código fuente
COPY src/ ./src/
COPY model/ ./model/

# Establecer variables de entorno
ENV PYTHONPATH=/app
ENV PYTHONUNBUFFERED=1

# Exponer puerto
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/api/v1/health || exit 1

# Comando para ejecutar la aplicación
CMD ["python", "-m", "src.main"] 