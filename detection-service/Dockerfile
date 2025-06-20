FROM python:3.12-slim as builder

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    && rm -rf /var/lib/apt/lists/*

# Install poetry
RUN pip install poetry

# Configure poetry
RUN poetry config virtualenvs.create false

# Copy dependency files first (for better caching)
COPY pyproject.toml poetry.lock* ./

# Install dependencies (this layer will be cached unless dependencies change)
RUN poetry install --no-interaction --no-ansi --no-root --only=main

# Final stage
FROM python:3.12-slim

WORKDIR /app

# Copy installed packages from builder
COPY --from=builder /usr/local/lib/python3.12/site-packages /usr/local/lib/python3.12/site-packages
COPY --from=builder /usr/local/bin /usr/local/bin

# Copy application code
COPY main.py .

# Copy model separately (this allows better layer caching)
COPY model/ ./model/

EXPOSE 8000

# Use uvicorn directly instead of poetry run
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
