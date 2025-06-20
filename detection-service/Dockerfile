# Use Python 3.12 slim image as base
FROM python:3.12-slim

# Set working directory
WORKDIR /app

# Install system dependencies required for TensorFlow and other packages
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    libc6-dev \
    libffi-dev \
    libssl-dev \
    && rm -rf /var/lib/apt/lists/*

# Install Poetry
RUN pip install poetry

# Copy Poetry configuration files
COPY pyproject.toml poetry.lock ./

# Configure Poetry to not create virtual environment (since we're in a container)
RUN poetry config virtualenvs.create false

# Regenerate lock file and install dependencies
RUN poetry lock && poetry install --only=main --no-interaction --no-ansi

# Copy application code
COPY . .

# Create directory for model if it doesn't exist
RUN mkdir -p model

# Copy the ML model (if not already copied)
COPY model/model-8678.keras model/

# Expose port
EXPOSE 8000

# Set environment variables
ENV PYTHONPATH=/app
ENV PYTHONUNBUFFERED=1

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/api/v1/detections/ || exit 1

# Run the application
CMD ["poetry", "run", "fastapi", "run", "main.py", "--host", "0.0.0.0", "--port", "8000"] 