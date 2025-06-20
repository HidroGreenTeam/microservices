import pika
import json
import os
import logging
from typing import Dict, Any
import requests
from datetime import datetime
import time

class MessagingService:
    def __init__(self):
        self.rabbitmq_host = os.getenv("RABBITMQ_HOST", "rabbitmq")
        self.rabbitmq_port = int(os.getenv("RABBITMQ_PORT", "5672"))
        self.rabbitmq_user = os.getenv("RABBITMQ_USER", "guest")
        self.rabbitmq_password = os.getenv("RABBITMQ_PASSWORD", "guest")
        self.user_service_url = os.getenv("USER_SERVICE_URL", "http://user-service:8081")
        
        self.connection = None
        self.channel = None
        self.setup_connection_with_retry()
    
    def setup_connection_with_retry(self, max_retries=5, delay=10):
        """Establece conexión con RabbitMQ con reintentos"""
        for attempt in range(max_retries):
            try:
                logging.info(f"=== INTENTO DE CONEXIÓN {attempt + 1}/{max_retries} ===")
                self.setup_connection()
                if self.connection and self.channel:
                    logging.info("✅ Conexión establecida exitosamente")
                    return
            except Exception as e:
                logging.warning(f"❌ Intento {attempt + 1} falló: {e}")
                if attempt < max_retries - 1:
                    logging.info(f"⏳ Esperando {delay} segundos antes del siguiente intento...")
                    time.sleep(delay)
                else:
                    logging.error("❌ Todos los intentos de conexión fallaron")
    
    def setup_connection(self):
        """Establece conexión con RabbitMQ"""
        try:
            logging.info("=== CONFIGURANDO CONEXIÓN RABBITMQ ===")
            logging.info("Host: %s, Port: %s, User: %s", self.rabbitmq_host, self.rabbitmq_port, self.rabbitmq_user)
            
            credentials = pika.PlainCredentials(self.rabbitmq_user, self.rabbitmq_password)
            parameters = pika.ConnectionParameters(
                host=self.rabbitmq_host,
                port=self.rabbitmq_port,
                credentials=credentials,
                heartbeat=600,
                blocked_connection_timeout=300,
                connection_attempts=3,
                retry_delay=5
            )
            
            logging.info("Conectando a RabbitMQ...")
            self.connection = pika.BlockingConnection(parameters)
            self.channel = self.connection.channel()
            
            # Declarar la cola para diagnósticos
            logging.info("Declarando cola 'diagnosis_queue'...")
            self.channel.queue_declare(queue='diagnosis_queue', durable=True)
            
            logging.info("✅ Conexión establecida con RabbitMQ exitosamente")
        except Exception as e:
            logging.error("❌ Error conectando a RabbitMQ: %s", e)
            self.connection = None
            self.channel = None
            raise e
    

    
    def send_diagnosis_message(self, diagnosis_data: Dict[str, Any]):
        """Envía mensaje de diagnóstico a la cola de RabbitMQ"""
        if not self.channel:
            logging.error("❌ No hay conexión con RabbitMQ")
            return False
        
        try:
            logging.info("=== ENVIANDO MENSAJE DE DIAGNÓSTICO ===")
            logging.info("Datos de diagnóstico: %s", diagnosis_data)
            
            # Preparar el mensaje
            message = {
                "diagnosis_id": diagnosis_data.get('diagnosis_id'),
                "crop_id": diagnosis_data.get('crop_id'),
                "profile_id": diagnosis_data.get('profile_id'),
                "predicted_class": diagnosis_data.get('predicted_class'),
                "confidence": diagnosis_data.get('confidence'),
                "disease_detected": diagnosis_data.get('disease_detected'),
                "image_url": diagnosis_data.get('image_url'),
                "analyzed_at": datetime.now().isoformat(),
                "requires_treatment": diagnosis_data.get('requires_treatment', False)
            }
            
            logging.info("Mensaje preparado: %s", message)
            
            # Enviar mensaje
            self.channel.basic_publish(
                exchange='',
                routing_key='diagnosis_queue',
                body=json.dumps(message),
                properties=pika.BasicProperties(
                    delivery_mode=2,  # make message persistent
                    content_type='application/json'
                )
            )
            
            logging.info("✅ Mensaje de diagnóstico enviado exitosamente: %s", diagnosis_data.get('diagnosis_id'))
            return True
            
        except Exception as e:
            logging.error("❌ Error enviando mensaje de diagnóstico: %s", e)
            return False
    
    def close_connection(self):
        """Cierra la conexión con RabbitMQ"""
        if self.connection and not self.connection.is_closed:
            self.connection.close()
            logging.info("Conexión con RabbitMQ cerrada")

# Instancia global del servicio de mensajería
messaging_service = MessagingService() 