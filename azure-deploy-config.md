# Configuración de Azure Container Apps para HidroGreen

## Variables de entorno necesarias para cada servicio

### Discovery Service
```bash
EUREKA_INSTANCE_HOSTNAME=discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### API Gateway
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=api-gateway.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### User Service
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### Crop Service
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=crop-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

### Notification Service
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=notification-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
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
FIREBASE_SERVER_KEY=your_firebase_server_key
```

### Report Service
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=report-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
```

### Subscription Service
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=subscription-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

### Treatment Service
```bash
EUREKA_SERVER_URL=https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/
EUREKA_INSTANCE_HOSTNAME=treatment-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
EUREKA_INSTANCE_SECURE_PORT=443
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```
docker build -t aynicontainer.azurecr.io/discovery-service:latest ./discovery-service; docker push aynicontainer.azurecr.io/discovery-service:latest;
docker build -t aynicontainer.azurecr.io/api-gateway:latest ./api-gateway; docker push aynicontainer.azurecr.io/api-gateway:latest;
docker build -t aynicontainer.azurecr.io/user-service:latest ./user-service; docker push aynicontainer.azurecr.io/user-service:latest;
docker build -t aynicontainer.azurecr.io/crop-service:latest ./crop-service; docker push aynicontainer.azurecr.io/crop-service:latest;
docker build -t aynicontainer.azurecr.io/notification-service:latest ./notification-service; docker push aynicontainer.azurecr.io/notification-service:latest;
docker build -t aynicontainer.azurecr.io/report-service:latest ./report-service; docker push aynicontainer.azurecr.io/report-service:latest;
docker build -t aynicontainer.azurecr.io/subscription-service:latest ./subscription-service; docker push aynicontainer.azurecr.io/subscription-service:latest;
docker build -t aynicontainer.azurecr.io/treatment-service:latest ./treatment-service; docker push aynicontainer.azurecr.io/treatment-service:latest


Name                  Location    ResourceGroup    Fqdn
--------------------  ----------  ---------------  ------------------------------------------------------------------------
discovery-service     East US     hidrogreenteam   discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
api-gateway           East US     hidrogreenteam   api-gateway.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io
user-service          East US     hidrogreenteam   user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
detection-service     East US     hidrogreenteam   detection-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/docs
treatment-service     East US     hidrogreenteam   treatment-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
crop-service          East US     hidrogreenteam   crop-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
report-service        East US     hidrogreenteam   report-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
notification-service  East US     hidrogreenteam   notification-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html
subscription-service  East US     hidrogreenteam   subscription-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/swagger-ui/index.html

## Instrucciones de Deploy

1. **Construir y subir las imágenes a Azure Container Registry**
2. **Configurar cada Container App con las variables de entorno correspondientes**
3. **Desplegar en el siguiente orden:**
   - Discovery Service (primero)
   - API Gateway
   - User Service
   - Resto de servicios

## Comandos Azure CLI (ejemplo)

```bash
# Configurar variables de entorno para una container app
az containerapp update \
  --name user-service \
  --resource-group your-resource-group \
  --set-env-vars \
    EUREKA_SERVER_URL="https://discovery-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io/eureka/" \
    EUREKA_INSTANCE_HOSTNAME="user-service.thankfulwater-e8adfc7e.eastus.azurecontainerapps.io" \
    EUREKA_INSTANCE_SECURE_PORT="443" \
    DATABASE_URL="jdbc:mysql://your-mysql-server:3306/ayni?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
    DATABASE_USERNAME="your_username" \
    DATABASE_PASSWORD="your_password"
```

## Notas importantes

- Reemplaza `YOUR_MYSQL_SERVER` con tu servidor MySQL en Azure
- Configura las credenciales reales de base de datos y RabbitMQ
- Asegúrate de que los servicios se despliegen en el orden correcto
- Verifica que los health checks estén funcionando antes de desplegar el siguiente servicio
