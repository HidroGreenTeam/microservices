from sqlalchemy import Column, Integer, String, DateTime, Boolean, Float, Text, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

class DiagnosisModel(Base):
    """Modelo SQLAlchemy para la tabla diagnosis"""
    
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