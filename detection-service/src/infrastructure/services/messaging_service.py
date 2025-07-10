from abc import ABC, abstractmethod
from typing import Dict, Any

class MessagingService(ABC):
    """Interfaz del servicio de mensajería"""
    
    @abstractmethod
    def send_diagnosis_message(self, diagnosis_data: Dict[str, Any]) -> bool:
        """
        Envía un mensaje de diagnóstico al sistema de mensajería
        
        Args:
            diagnosis_data: Datos del diagnóstico a enviar
            
        Returns:
            True si se envió exitosamente, False en caso contrario
        """
        pass
    
    @abstractmethod
    def close_connection(self):
        """Cierra la conexión con el sistema de mensajería"""
        pass 