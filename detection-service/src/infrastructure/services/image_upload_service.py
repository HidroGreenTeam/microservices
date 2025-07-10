from abc import ABC, abstractmethod
from typing import Optional, Dict, Any
from fastapi import UploadFile

class ImageUploadService(ABC):
    """Interfaz del servicio de subida de imágenes"""
    
    @abstractmethod
    async def upload_image(self, file: UploadFile, folder: str = "hidrogreen/diagnosis") -> Optional[Dict[str, Any]]:
        """
        Sube una imagen al servicio de almacenamiento
        
        Args:
            file: El archivo a subir
            folder: Carpeta donde guardar la imagen
            
        Returns:
            Dictionary con información de la imagen subida o None si falla
        """
        pass
    
    @abstractmethod
    async def delete_image(self, public_id: str) -> bool:
        """
        Elimina una imagen del servicio de almacenamiento
        
        Args:
            public_id: ID público de la imagen
            
        Returns:
            True si se eliminó exitosamente, False en caso contrario
        """
        pass
    
    @abstractmethod
    async def get_image_info(self, public_id: str) -> Optional[Dict[str, Any]]:
        """
        Obtiene información de una imagen
        
        Args:
            public_id: ID público de la imagen
            
        Returns:
            Dictionary con información de la imagen o None si no existe
        """
        pass 