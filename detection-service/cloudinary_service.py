import cloudinary
import cloudinary.uploader
import os
import logging
from typing import Optional
import io
from PIL import Image

logger = logging.getLogger(__name__)

class CloudinaryService:
    def __init__(self):
        """Inicializa el servicio de Cloudinary con credenciales de HidroGreen"""
        # Credenciales de HidroGreen Cloudinary
        self.cloud_name = "deu4nwmqh"
        self.api_key = "789752667392435"
        self.api_secret = "QlOIvCICBryMf5qy2HryHoJMpUQ"
        
        cloudinary.config(
            cloud_name=self.cloud_name,
            api_key=self.api_key,
            api_secret=self.api_secret
        )
        self.configured = True
        logger.info("✅ Cloudinary configurado correctamente con credenciales de HidroGreen")
    
    def upload_diagnosis_image(self, image_bytes: bytes, diagnosis_id: int, crop_id: int) -> Optional[str]:
        """
        Sube imagen de diagnóstico a Cloudinary
        
        Args:
            image_bytes: Bytes de la imagen
            diagnosis_id: ID del diagnóstico
            crop_id: ID del cultivo
            
        Returns:
            URL de la imagen subida o None si falla
        """
        if not self.configured:
            logger.error("❌ Cloudinary no está configurado")
            return None
            
        try:
            logger.info(f"📤 Subiendo imagen de diagnóstico {diagnosis_id} para cultivo {crop_id}")
            
            # Crear un nombre único para la imagen
            public_id = f"diagnosis/crop_{crop_id}/diagnosis_{diagnosis_id}"
            
            # Subir imagen a Cloudinary
            result = cloudinary.uploader.upload(
                image_bytes,
                public_id=public_id,
                folder="hidrogreen/diagnosis",
                resource_type="image",
                format="jpg",
                quality="auto:good",
                fetch_format="auto",
                transformation=[
                    {"width": 800, "height": 800, "crop": "limit"},
                    {"quality": "auto:good"}
                ],
                tags=[f"crop_{crop_id}", "diagnosis", "ml_analysis"]
            )
            
            image_url = result.get('secure_url')
            logger.info(f"✅ Imagen subida exitosamente: {image_url}")
            
            return image_url
            
        except Exception as e:
            logger.error(f"❌ Error subiendo imagen a Cloudinary: {e}")
            return None
    
    def delete_diagnosis_image(self, image_url: str) -> bool:
        """
        Elimina imagen de diagnóstico de Cloudinary
        
        Args:
            image_url: URL de la imagen a eliminar
            
        Returns:
            True si se eliminó exitosamente, False si falló
        """
        if not self.configured:
            logger.error("❌ Cloudinary no está configurado")
            return False
            
        try:
            # Extraer public_id de la URL
            public_id = self._extract_public_id_from_url(image_url)
            if not public_id:
                logger.error("❌ No se pudo extraer public_id de la URL")
                return False
                
            logger.info(f"🗑️ Eliminando imagen: {public_id}")
            
            result = cloudinary.uploader.destroy(public_id)
            
            if result.get('result') == 'ok':
                logger.info(f"✅ Imagen eliminada exitosamente: {public_id}")
                return True
            else:
                logger.warning(f"⚠️ No se pudo eliminar la imagen: {result}")
                return False
                
        except Exception as e:
            logger.error(f"❌ Error eliminando imagen de Cloudinary: {e}")
            return False
    
    def _extract_public_id_from_url(self, url: str) -> Optional[str]:
        """Extrae el public_id de una URL de Cloudinary"""
        try:
            # URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{transformations}/{public_id}.{format}
            parts = url.split('/')
            if 'cloudinary.com' in url and len(parts) > 6:
                # Encontrar la parte después de 'upload'
                upload_index = parts.index('upload')
                if upload_index + 1 < len(parts):
                    # El public_id puede tener carpetas, así que unimos todo después de upload
                    public_id_with_extension = '/'.join(parts[upload_index + 1:])
                    # Remover la extensión del archivo
                    public_id = public_id_with_extension.rsplit('.', 1)[0]
                    return public_id
            return None
        except Exception as e:
            logger.error(f"Error extrayendo public_id: {e}")
            return None

# Instancia global del servicio
cloudinary_service = CloudinaryService() 