import cloudinary
import cloudinary.uploader
import cloudinary.api
import os
import logging
from typing import Optional, Dict, Any
from fastapi import UploadFile

from ...infrastructure.services.image_upload_service import ImageUploadService

logger = logging.getLogger(__name__)

class CloudinaryImageUploadService(ImageUploadService):
    """Implementación del servicio de subida de imágenes usando Cloudinary"""
    
    def __init__(self):
        # Configurar Cloudinary con las credenciales
        cloudinary.config(
            cloud_name="deu4nwmqh",
            api_key="789752667392435",
            api_secret="QlOIvCICBryMf5qy2HryHoJMpUQ"
        )
        logger.info("Cloudinary configurado correctamente")
    
    async def upload_image(self, file: UploadFile, folder: str = "hidrogreen/diagnosis") -> Optional[Dict[str, Any]]:
        """
        Sube una imagen a Cloudinary
        
        Args:
            file: El archivo a subir
            folder: Carpeta donde guardar la imagen
            
        Returns:
            Dictionary con información de la imagen subida o None si falla
        """
        try:
            # Leer el contenido del archivo
            contents = await file.read()
            
            # Subir a Cloudinary
            result = cloudinary.uploader.upload(
                contents,
                folder=folder,
                resource_type="image",
                use_filename=True,
                unique_filename=True
            )
            
            logger.info(f"Imagen subida exitosamente: {result['public_id']}")
            
            return {
                "url": result["secure_url"],
                "public_id": result["public_id"],
                "format": result["format"],
                "width": result["width"],
                "height": result["height"],
                "bytes": result["bytes"]
            }
            
        except Exception as e:
            logger.error(f"Error al subir imagen a Cloudinary: {e}")
            return None
    
    async def delete_image(self, public_id: str) -> bool:
        """
        Elimina una imagen de Cloudinary
        
        Args:
            public_id: ID público de la imagen en Cloudinary
            
        Returns:
            True si se eliminó exitosamente, False en caso contrario
        """
        try:
            result = cloudinary.uploader.destroy(public_id)
            logger.info(f"Imagen eliminada: {public_id}")
            return result["result"] == "ok"
        except Exception as e:
            logger.error(f"Error al eliminar imagen de Cloudinary: {e}")
            return False
    
    async def get_image_info(self, public_id: str) -> Optional[Dict[str, Any]]:
        """
        Obtiene información de una imagen de Cloudinary
        
        Args:
            public_id: ID público de la imagen
            
        Returns:
            Dictionary con información de la imagen o None si no existe
        """
        try:
            result = cloudinary.api.resource(public_id)
            return result
        except Exception as e:
            logger.error(f"Error al obtener información de imagen: {e}")
            return None 