# 🌱 HidroGreen Microservices

Sistema de microservicios para la gestión de cultivos hidropónicos con detección de enfermedades mediante IA.

## 📋 Arquitectura del Sistema

El proyecto está compuesto por los siguientes microservicios:

### 🔧 Servicios de Infraestructura
- **Discovery Service** (Eureka) - Puerto 8761
- **API Gateway** - Puerto 8080
- **MySQL Database** - Puerto 3307
- **RabbitMQ** - Puerto 5672 (Management: 15672)

### 🚀 Microservicios de Negocio
- **User Service** - Puerto 8081 (Gestión de usuarios)
- **Treatment Service** - Puerto 8082 (Gestión de tratamientos)
- **Report Service** - Puerto 8083 (Generación de reportes)
- **Notification Service** - Puerto 8084 (Notificaciones por email/SMS/WhatsApp)
- **Crop Service** - Puerto 8085 (Gestión de cultivos)
- **Detection Service** - Puerto 8000 (Detección de enfermedades con IA - Python/FastAPI)

## 🛠️ Tecnologías Utilizadas

- **Backend**: Spring Boot 3.5, Java 21
- **IA**: Python 3.12, FastAPI, TensorFlow
- **Base de Datos**: MySQL 8.0
- **Mensajería**: RabbitMQ
- **Containerización**: Docker & Docker Compose
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway

## 🚀 Compilación y Ejecución con Docker

### Prerrequisitos
- Docker Desktop instalado
- Docker Compose instalado
- Git instalado

### 📋 Pasos para ejecutar el proyecto

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/HidroGreenTeam/microservices.git
   cd microservices
   ```

2. **Configurar variables de entorno (opcional)**
   ```bash
   # Copiar el archivo de ejemplo
   cp .env.example .env
   
   # Editar .env con tus credenciales reales
   notepad .env
   ```

3. **Ejecutar todos los servicios**
   ```bash
   # Construir y ejecutar en segundo plano
   docker-compose up --build -d
   
   # Ver logs en tiempo real
   docker-compose logs -f
   ```

4. **Verificar que los servicios están funcionando**
   ```bash
   # Ver estado de los contenedores
   docker-compose ps
   
   # Verificar servicios individuales
   curl http://localhost:8080  # API Gateway
   curl http://localhost:8761  # Eureka Dashboard
   curl http://localhost:8000  # Detection Service
   ```

### 🔧 Comandos útiles

```bash
# Detener todos los servicios
docker-compose down

# Reconstruir un servicio específico
docker-compose up --build user-service

# Ver logs de un servicio específico
docker-compose logs notification-service

# Limpiar todo (contenedores, volúmenes, redes)
docker-compose down --volumes --remove-orphans
docker system prune -f
```

## 🌐 Endpoints de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| API Gateway | http://localhost:8080 | Punto de entrada principal |
| Eureka Dashboard | http://localhost:8761 | Registro de servicios |
| Detection Service | http://localhost:8000 | API de detección de enfermedades |
| RabbitMQ Management | http://localhost:15672 | Panel de administración (guest/guest) |
| User Service | http://localhost:8081/actuator/health | Health check |
| Treatment Service | http://localhost:8082/actuator/health | Health check |
| Report Service | http://localhost:8083/actuator/health | Health check |
| Notification Service | http://localhost:8084/actuator/health | Health check |
| Crop Service | http://localhost:8085/actuator/health | Health check |

## 🔐 Configuración de Variables de Entorno

Para usar las funcionalidades completas del sistema, configura las siguientes variables de entorno:

### Email (Gmail SMTP)
```env
EMAIL_USERNAME=tu_email@gmail.com
EMAIL_PASSWORD=tu_app_password_de_gmail
EMAIL_FROM_ADDRESS=tu_email@gmail.com
```

### Twilio (SMS/WhatsApp)
```env
TWILIO_ACCOUNT_SID=ACtu_account_sid_aqui
TWILIO_AUTH_TOKEN=tu_auth_token_aqui
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
```

### Push Notifications
```env
# Firebase has been removed as per requirements
# Push notifications are handled through email and SMS only
```

## 📂 Estructura del Proyecto

```
microservices/
├── api-gateway/          # Spring Cloud Gateway
├── discovery-service/    # Eureka Server
├── user-service/         # Gestión de usuarios
├── treatment-service/    # Gestión de tratamientos
├── crop-service/         # Gestión de cultivos
├── report-service/       # Generación de reportes
├── notification-service/ # Notificaciones
├── detection-service/    # IA para detección (Python)
├── docker-compose.yml    # Configuración Docker
└── README.md            # Este archivo
```

## 🔄 Orden de Inicio de Servicios

Docker Compose maneja automáticamente las dependencias:

1. **Infraestructura**: MySQL, RabbitMQ
2. **Discovery Service**: Eureka
3. **Microservicios**: user, treatment, crop, report, notification
4. **Detection Service**: Servicio de IA en Python
5. **API Gateway**: Punto de entrada

## 🐛 Solución de Problemas

### Problemas comunes

1. **Puerto ocupado**: Cambiar puertos en docker-compose.yml
2. **Memoria insuficiente**: Asignar más memoria a Docker Desktop
3. **Servicios no se conectan**: Verificar que la red `hidrogreen-network` esté creada

### Logs y debugging

```bash
# Ver logs de todos los servicios
docker-compose logs

# Ver logs de un servicio específico
docker-compose logs detection-service

# Entrar a un contenedor para debugging
docker exec -it hidrogreen-user-service bash
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Equipo

- **HidroGreen Team** - Desarrollo inicial - [HidroGreenTeam](https://github.com/HidroGreenTeam)

---

🌱 **HidroGreen** - Innovando en agricultura hidropónica con tecnología de vanguardia
