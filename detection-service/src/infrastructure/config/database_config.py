import os
import logging
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base

from ..persistence.models import Base

logger = logging.getLogger(__name__)

class DatabaseConfig:
    """Configuración de la base de datos"""
    
    def __init__(self):
        self.database_url = os.getenv("DATABASE_URL", "mysql+pymysql://root:root@localhost:3307/ayni")
        self.engine = None
        self.SessionLocal = None
        self._setup_database()
    
    def _setup_database(self):
        """Configura la conexión a la base de datos"""
        try:
            logger.info(f"Configurando base de datos: {self.database_url}")
            self.engine = create_engine(self.database_url, echo=True)
            self.SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=self.engine)
            logger.info("Base de datos configurada exitosamente")
        except Exception as e:
            logger.error(f"Error al configurar base de datos: {e}")
            raise
    
    def create_tables(self):
        """Crea las tablas en la base de datos"""
        try:
            Base.metadata.create_all(bind=self.engine)
            logger.info("Tablas creadas exitosamente")
        except Exception as e:
            logger.error(f"Error al crear tablas: {e}")
            raise
    
    def get_session(self):
        """Obtiene una sesión de base de datos"""
        if not self.SessionLocal:
            raise Exception("Base de datos no configurada")
        
        db = self.SessionLocal()
        try:
            yield db
        finally:
            db.close()

# Instancia global de configuración
database_config = DatabaseConfig() 