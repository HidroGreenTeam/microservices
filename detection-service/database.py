import os
from sqlalchemy import create_engine, Column, Integer, String, DateTime, Boolean, Float, Text, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session, relationship
from datetime import datetime
from typing import Optional
import logging

# Configurar logging
logger = logging.getLogger(__name__)

# Configuración de la base de datos
DATABASE_URL = os.getenv("DATABASE_URL", "mysql+pymysql://root:root@localhost:3307/ayni")

engine = create_engine(DATABASE_URL, echo=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

class Diagnosis(Base):
    __tablename__ = "diagnosis"
    
    id = Column(Integer, primary_key=True, index=True)
    crop_id = Column(Integer, nullable=False)
    profile_id = Column(Integer, nullable=False)
    predicted_class = Column(String(100), nullable=False)
    confidence = Column(Float, nullable=False)
    disease_detected = Column(Boolean, nullable=False)
    requires_treatment = Column(Boolean, nullable=False)
    image_url = Column(String(500), nullable=False)
    image_public_id = Column(String(255), nullable=True)  # Para Cloudinary
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def create_tables():
    """Crear las tablas en la base de datos"""
    try:
        Base.metadata.create_all(bind=engine)
        logger.info("Tablas creadas exitosamente")
    except Exception as e:
        logger.error(f"Error al crear tablas: {e}")
