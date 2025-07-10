import os
import logging
from typing import Dict, Any

logger = logging.getLogger(__name__)

class EurekaConfig:
    """Configuración para el cliente Eureka"""
    
    @staticmethod
    def get_eureka_config() -> Dict[str, Any]:
        """Obtiene la configuración completa para Eureka"""
        
        # Variables de entorno
        eureka_server = os.getenv("EUREKA_SERVER", "https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka")
        service_host = os.getenv("EUREKA_INSTANCE_HOSTNAME", "detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io")
        secure_port = int(os.getenv("EUREKA_INSTANCE_SECURE_PORT", "443"))
        
        # Configuración base
        config = {
            "eureka_server": eureka_server,
            "app_name": "detection-service",
            "instance_host": service_host,
            "instance_port": secure_port,
            "instance_secure_port_enabled": True,
            "home_page_url": f"https://{service_host}/",
            "status_page_url": f"https://{service_host}/api/v1/health",
            "health_check_url": f"https://{service_host}/api/v1/health",
            "instance_metadata": {
                "management.port": str(secure_port),
                "management.context-path": "/api/v1",
                "securePort": str(secure_port),
                "securePortEnabled": "true",
                "instanceId": f"{service_host}:detection-service:{secure_port}",
                "hostName": service_host,
                "app": "detection-service",
                "ipAddr": service_host,  # Usar hostname en lugar de IP
                "vipAddress": "detection-service",
                "secureVipAddress": "detection-service",
                "status": "UP",
                "dataCenterInfo": {
                    "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                    "name": "MyOwn"
                }
            }
        }
        
        logger.info(f"Configuración Eureka generada: {config}")
        return config 