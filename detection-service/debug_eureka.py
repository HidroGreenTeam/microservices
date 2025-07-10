#!/usr/bin/env python3
"""
Script de diagnóstico para verificar el estado del registro en Eureka
"""

import requests
import json
import os
from dotenv import load_dotenv

# Cargar variables de entorno
load_dotenv()

def check_eureka_registry():
    """Verifica el estado del registro en Eureka"""
    
    eureka_server = os.getenv("EUREKA_SERVER", "https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka")
    service_host = os.getenv("EUREKA_INSTANCE_HOSTNAME", "detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io")
    
    print("=== DIAGNÓSTICO EUREKA ===")
    print(f"Eureka Server: {eureka_server}")
    print(f"Service Host: {service_host}")
    print()
    
    try:
        # Verificar si Eureka está disponible
        print("1. Verificando disponibilidad de Eureka...")
        response = requests.get(f"{eureka_server}/apps", timeout=10)
        if response.status_code == 200:
            print("✅ Eureka está disponible")
            
            # Obtener información de las aplicaciones registradas
            apps_data = response.json()
            print(f"📊 Aplicaciones registradas: {len(apps_data.get('applications', {}).get('application', []))}")
            
            # Buscar detection-service
            detection_service = None
            for app in apps_data.get('applications', {}).get('application', []):
                if app.get('name') == 'DETECTION-SERVICE':
                    detection_service = app
                    break
            
            if detection_service:
                print("✅ Detection Service encontrado en Eureka")
                instances = detection_service.get('instance', [])
                print(f"📋 Instancias registradas: {len(instances)}")
                
                for i, instance in enumerate(instances):
                    print(f"\n--- Instancia {i+1} ---")
                    print(f"Host: {instance.get('hostName')}")
                    print(f"IP: {instance.get('ipAddr')}")
                    print(f"Puerto: {instance.get('port', {}).get('$')}")
                    print(f"Puerto Seguro: {instance.get('securePort', {}).get('$')}")
                    print(f"Estado: {instance.get('status')}")
                    print(f"URL Home: {instance.get('homePageUrl')}")
                    print(f"URL Health: {instance.get('healthCheckUrl')}")
            else:
                print("❌ Detection Service NO encontrado en Eureka")
                
        else:
            print(f"❌ Eureka no está disponible. Status: {response.status_code}")
            
    except Exception as e:
        print(f"❌ Error al conectar con Eureka: {e}")
    
    print("\n=== VERIFICACIÓN DE VARIABLES DE ENTORNO ===")
    env_vars = [
        "EUREKA_SERVER",
        "EUREKA_INSTANCE_HOSTNAME", 
        "EUREKA_INSTANCE_SECURE_PORT",
        "USER_SERVICE_URL",
        "RABBITMQ_HOST",
        "DATABASE_URL"
    ]
    
    for var in env_vars:
        value = os.getenv(var)
        if value:
            # Ocultar contraseñas
            if "PASSWORD" in var or "DATABASE_URL" in var:
                masked_value = value.split("@")[0] + "@***" if "@" in value else "***"
                print(f"{var}: {masked_value}")
            else:
                print(f"{var}: {value}")
        else:
            print(f"{var}: ❌ NO DEFINIDA")

if __name__ == "__main__":
    check_eureka_registry() 