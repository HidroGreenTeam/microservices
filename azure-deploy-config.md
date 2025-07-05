# Configuración de Azure Container Apps para HidroGreen

## 🚀 NUEVA ESTRATEGIA DE PERFILES

### ✅ **DESARROLLO LOCAL (Docker)**
```bash
# Simplemente ejecutar Docker Compose
docker-compose up
```

### ✅ **PRODUCCIÓN AZURE**
```bash
# Solo agregar esta variable de entorno en Azure Container Apps
SPRING_PROFILES_ACTIVE=prod
```

## 📋 Variables de entorno necesarias para PRODUCCIÓN (Azure)

### Discovery Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### API Gateway
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=api-gateway.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### User Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
DATABASE_URL=jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

### Treatment Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=treatment-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
DATABASE_URL=jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

### Report Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=report-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
DATABASE_URL=jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

### Notification Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=notification-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
DATABASE_URL=jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_app_password
EMAIL_FROM_ADDRESS=your_email@gmail.com
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
# Firebase removed as per requirements
```

### Crop Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=crop-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
DATABASE_URL=jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

### Subscription Service
```bash
SPRING_PROFILES_ACTIVE=prod
EUREKA_INSTANCE_HOSTNAME=subscription-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
DATABASE_URL=jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

### Payment Gateway Service
```bash
SPRING_PROFILES_ACTIVE=prod
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
```

### Detection Service (Python)
```bash
EUREKA_SERVER=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
SERVICE_HOST=detection-service
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USER=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
USER_SERVICE_URL=https://user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
```

## 🚀 **COMANDOS DE DEPLOY SÚPER SIMPLES**

### 1. Construir y subir imágenes
```bash
docker build -t aynicontainer.azurecr.io/discovery-service:latest ./discovery-service; docker push aynicontainer.azurecr.io/discovery-service:latest;
docker build -t aynicontainer.azurecr.io/api-gateway:latest ./api-gateway; docker push aynicontainer.azurecr.io/api-gateway:latest;
docker build -t aynicontainer.azurecr.io/user-service:latest ./user-service; docker push aynicontainer.azurecr.io/user-service:latest;
docker build -t aynicontainer.azurecr.io/treatment-service:latest ./treatment-service; docker push aynicontainer.azurecr.io/treatment-service:latest;
docker build -t aynicontainer.azurecr.io/report-service:latest ./report-service; docker push aynicontainer.azurecr.io/report-service:latest;
docker build -t aynicontainer.azurecr.io/notification-service:latest ./notification-service; docker push aynicontainer.azurecr.io/notification-service:latest;
docker build -t aynicontainer.azurecr.io/crop-service:latest ./crop-service; docker push aynicontainer.azurecr.io/crop-service:latest;
docker build -t aynicontainer.azurecr.io/subscription-service:latest ./subscription-service; docker push aynicontainer.azurecr.io/subscription-service:latest;
docker build -t aynicontainer.azurecr.io/payment-gateway:latest ./hidrogreen-payment-gateway-service; docker push aynicontainer.azurecr.io/payment-gateway:latest;
docker build -t aynicontainer.azurecr.io/detection-service:latest ./detection-service; docker push aynicontainer.azurecr.io/detection-service:latest;
```

### 2. Configurar Container Apps
```bash
# Ejemplo para user-service
az containerapp update \
  --name user-service \
  --resource-group your-resource-group \
  --set-env-vars \
    SPRING_PROFILES_ACTIVE="prod" \
    EUREKA_INSTANCE_HOSTNAME="user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io" \
    EUREKA_INSTANCE_SECURE_PORT="443" \
    DATABASE_URL="jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
    DATABASE_USERNAME="your_username" \
    DATABASE_PASSWORD="your_password"
```

## 🎯 **VENTAJAS DE LA NUEVA ESTRATEGIA**

- ✅ **Desarrollo**: `docker-compose up` y listo
- ✅ **Producción**: Solo agregar `SPRING_PROFILES_ACTIVE=prod`
- ✅ **Sin edición de archivos**: Nunca más cambiar configuraciones
- ✅ **Consistente**: Todos los servicios usan la misma estrategia
- ✅ **Mantenible**: Fácil agregar nuevos entornos

## 📋 **URLs de Servicios en Producción**

```
Discovery Service: https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
API Gateway:       https://api-gateway.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
User Service:      https://user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
Treatment Service: https://treatment-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
Report Service:    https://report-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
Notification Service: https://notification-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
Crop Service:      https://crop-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
Subscription Service: https://subscription-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
Payment Gateway:   https://payment-gateway.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
Detection Service: https://detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/docs
```

## 🔄 **ORDEN DE DESPLIEGUE**

1. **Discovery Service** (primero)
2. **API Gateway**
3. **User Service**
4. **Resto de servicios** (en paralelo)

## ⚠️ **NOTAS IMPORTANTES**

- Todos los servicios ahora usan **perfiles automáticos**
- **Docker**: Perfil `docker` por defecto
- **Azure**: Solo agregar `SPRING_PROFILES_ACTIVE=prod`
- **Configuración centralizada** por entorno
- **Nunca más editar archivos** para deploy
