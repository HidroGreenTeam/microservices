# 🌾 Ayni - Microservices Platform

## 🚀 **ARQUITECTURA OPTIMIZADA** (Actualizada)

### **📊 Microservicios**

| Microservicio | Puerto | Descripción | Base de Datos |
|---------------|--------|-------------|---------------|
| **Discovery Service** | `8761` | Eureka Server | - |
| **API Gateway** | `8080` | Puerta de entrada única | - |
| **User Service** | `8081` | **Usuarios integrados con perfiles** | MySQL (`ayni`) |
| **Crop Service** | `8085` | Gestión de cultivos | MySQL (`ayni`) |
| **Treatment Service** | `8082` | Diagnósticos y tratamientos | MySQL (`ayni`) |
| **Detection Service** | `8000` | IA para detección de enfermedades | - |
| **Notification Service** | `8084` | Notificaciones | MySQL (`ayni`) |
| **Report Service** | `8083` | Reportes y análisis | MySQL (`ayni`) |
| **Payment Gateway** | `8086` | Pasarela de pagos | - |

### **🎯 OPTIMIZACIONES IMPLEMENTADAS**

#### **✅ User Service - Arquitectura Unificada**
- **Antes**: User (auth) + Farmer (profile) = 2 entidades, 2 IDs, datos duplicados
- **Después**: User único con perfil integrado = 1 entidad, 1 ID, sin duplicación

```java
// Nueva entidad User optimizada
@Entity
public class User {
    private Long id;                    // Un solo ID
    private String fullName;            // Identificación
    private String email;               // Único
    private String password;            // Hasheado (único)
    private Set<Role> roles;            // Permisos
    
    // Perfil integrado (antes en Farmer)
    private String username;            // Nombre de usuario
    private String phoneNumber;         // Teléfono  
    private String imageUrl;            // Imagen de perfil
}
```

#### **🔗 Endpoints de Compatibilidad**
- `/api/v1/users/farmers` → Lista de usuarios con rol farmer
- `/api/v1/users/farmers/{id}` → Farmer por ID
- `/api/v1/users/farmers/exists` → Verificar existencia
- `/api/v1/users/farmers/{id}/profile` → Actualizar perfil

### **🗄️ Base de Datos MySQL**

**Puerto**: `3307`  
**Conexión**: `jdbc:mysql://localhost:3307/ayni`  
**Credenciales**: `root/root`

### **🌐 URLs de Acceso**

#### **APIs Swagger UI**
- **User Service**: http://localhost:8081/swagger-ui.html
- **Crop Service**: http://localhost:8085/swagger-ui.html
- **Treatment Service**: http://localhost:8082/swagger-ui.html
- **Detection Service**: http://localhost:8000/docs
- **Notification Service**: http://localhost:8084/swagger-ui.html
- **Report Service**: http://localhost:8083/swagger-ui.html

#### **Infrastructura**
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (`guest/guest`)

### **🏃 Ejecución**

#### **1. Levantar Infraestructura**
```bash
docker-compose up -d mysql rabbitmq
```

#### **2. Levantar Discovery Service**
```bash
cd discovery-service && mvn spring-boot:run
```

#### **3. Levantar User Service (optimizado)**
```bash
cd user-service && mvn spring-boot:run
```

#### **4. Levantar otros microservicios**
```bash
# En terminales separadas
cd crop-service && mvn spring-boot:run
cd treatment-service && mvn spring-boot:run  
cd detection-service && python main.py
cd notification-service && mvn spring-boot:run
cd report-service && mvn spring-boot:run
```

### **🔄 Migración de Datos Existentes**

Si tienes datos anteriores con tablas separadas `users` y `farmers`:

```sql
-- Migrar datos de farmers a users
UPDATE users u 
JOIN farmers f ON u.email = f.email 
SET 
  u.username = f.username,
  u.phone_number = f.phone_number,
  u.image_url = COALESCE(fi.image_url, null)
FROM farmers f
LEFT JOIN farmer_images fi ON f.farmer_image_id = fi.id
WHERE u.email = f.email;

-- Actualizar referencias en crops
UPDATE crops c
JOIN farmers f ON c.farmer_id = f.id
JOIN users u ON f.email = u.email
SET c.farmer_id = u.id;

-- Eliminar tablas obsoletas (después de verificar)
-- DROP TABLE farmers;
-- DROP TABLE farmer_images;
```

### **✨ Beneficios de la Optimización**

1. **🎯 Simplicidad**: Una sola entidad User con perfil integrado
2. **⚡ Performance**: Sin JOINs innecesarios entre User-Farmer  
3. **🔒 Consistencia**: Un solo ID, sin duplicación de datos
4. **🛠️ Mantenibilidad**: Menos código, menos complejidad
5. **🔄 Escalabilidad**: Arquitectura más limpia para futuras features
6. **🐛 Menos Bugs**: Sin sincronización entre entidades separadas

### **🎨 API Documentation**

Todos los microservicios usan **Ayni** como título de API:
- ✅ **Ayni User Service API**
- ✅ **Ayni Crop Service API** 
- ✅ **Ayni Treatment Service API**
- ✅ **Ayni Detection Service API**
- ✅ **Ayni Notification Service API**
- ✅ **Ayni Report Service API**

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

### Firebase (Push Notifications)
```env
FIREBASE_SERVER_KEY=tu_firebase_server_key_aqui
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

### Firebase (Push Notifications)
```env
FIREBASE_SERVER_KEY=tu_firebase_server_key_aqui
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
