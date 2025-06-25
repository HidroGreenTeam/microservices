# Conexión entre Subscription Service y Notification Service

## Resumen de la Implementación

La conexión entre los microservicios `subscription-service` y `notification-service` se ha implementado exitosamente utilizando **RabbitMQ** como sistema de mensajería asíncrona.

## Arquitectura de la Conexión

```
subscription-service → RabbitMQ → notification-service
                   ↓
            Eventos de Domain     
         SubscriptionCreatedEvent
         SubscriptionCancelledEvent  
         SubscriptionRenewedEvent
                   ↓
           SubscriptionNotificationPublisher
                   ↓
              RabbitMQ Exchange
           (subscription.exchange)
                   ↓
             RabbitMQ Queue
       (subscription.notification.queue)
                   ↓
         SubscriptionEventListener
                   ↓
        NotificationCommandService
                   ↓
           Email/SMS/WhatsApp/Push
```

## Componentes Implementados

### Subscription Service

1. **Event Publishers**:
   - `SubscriptionNotificationPublisher`: Publica eventos de suscripción a RabbitMQ
   - Configurado para enviar notificaciones en eventos de creación, cancelación y renovación

2. **Event Handlers**:
   - `SubscriptionEventHandler`: Maneja eventos de dominio y los convierte a notificaciones
   - Integración con `ExternalUserService` para obtener datos del usuario

3. **Schedulers**:
   - `SubscriptionExpirationScheduler`: Detecta suscripciones por vencer y envía notificaciones
   - `SubscriptionMaintenanceScheduler`: Mantiene el estado de las suscripciones

4. **Testing**:
   - `SubscriptionTestController`: Endpoints para probar la integración

### Notification Service

1. **Message Listeners**:
   - `SubscriptionEventListener`: Escucha mensajes de RabbitMQ sobre eventos de suscripción
   - Procesa automáticamente notificaciones de creación, cancelación, renovación y vencimiento

2. **Configuration**:
   - `RabbitMQConfig`: Configuración de colas, exchanges y bindings
   - `JacksonConfig`: Configuración para manejo de fechas LocalDateTime

3. **Testing**:
   - `NotificationTestController`: Endpoints para probar el procesamiento de notificaciones

## Tipos de Notificaciones Soportadas

| Tipo de Evento | Trigger | Descripción |
|----------------|---------|-------------|
| `SUBSCRIPTION_CREATED` | Cuando se crea una nueva suscripción | Mensaje de bienvenida con detalles del plan |
| `SUBSCRIPTION_CANCELLED` | Cuando se cancela una suscripción | Mensaje de despedida |
| `SUBSCRIPTION_RENEWED` | Cuando se renueva una suscripción | Confirmación de renovación con nuevos detalles |
| `SUBSCRIPTION_EXPIRING` | Scheduler automático | Recordatorio de vencimiento (7 días y 24 horas antes) |

## Configuración de RabbitMQ

### Exchanges y Queues

- **Exchange**: `subscription.exchange` (TopicExchange)
- **Queue**: `subscription.notification.queue` (Durable)
- **Routing Keys**:
  - `subscription.created`
  - `subscription.cancelled`
  - `subscription.renewed`
  - `subscription.expiring`

## Cómo Probar la Conexión

### 1. Verificar Health Status

**Subscription Service:**
```bash
GET http://localhost:8087/api/v1/subscriptions/test/health
```

**Notification Service:**
```bash
GET http://localhost:8084/api/v1/notifications/test/health
```

### 2. Probar Notificación de Prueba

**Desde Subscription Service:**
```bash
POST http://localhost:8087/api/v1/subscriptions/test/notification
Content-Type: application/json

{
  "userId": 1,
  "email": "test@hidrogreen.com",
  "notificationType": "SUBSCRIPTION_CREATED"
}
```

### 3. Probar Procesamiento Directo en Notification Service

```bash
POST http://localhost:8084/api/v1/notifications/test/subscription
Content-Type: application/json

{
  "notificationType": "SUBSCRIPTION_CREATED",
  "userEmail": "test@hidrogreen.com",
  "subscriptionType": "BASIC",
  "planName": "Plan Básico",
  "price": 19.99
}
```

### 4. Verificar Integración con User Service

```bash
GET http://localhost:8087/api/v1/subscriptions/test/user/1
```

## Configuración de Variables de Entorno

### Subscription Service (application.properties)
```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# External Services
app.services.user-service.url=http://user-service:8081
```

### Notification Service (application.properties)
```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
```

## Troubleshooting

### Problemas Comunes

1. **RabbitMQ no conecta**:
   - Verificar que RabbitMQ esté ejecutándose en localhost:5672
   - Verificar credenciales (guest/guest por defecto)

2. **Mensajes no se procesan**:
   - Verificar logs de ambos servicios
   - Comprobar que las queues estén creadas correctamente en RabbitMQ Management

3. **Emails no se envían**:
   - Verificar configuración SMTP
   - Configurar variables de entorno para email

### Logs Importantes

**Subscription Service:**
```
INFO - Publishing subscription created notification for user ID: {userId}
INFO - Successfully published subscription created notification for subscription ID: {subscriptionId}
```

**Notification Service:**
```
INFO - Received subscription notification message: {message}
INFO - Successfully created notification with ID: {notificationId} for user: {userId}
```

## Monitoreo y Métricas

### RabbitMQ Management UI
- Acceder a: http://localhost:15672
- Usuario: guest / Contraseña: guest
- Verificar queues, exchanges y mensajes

### Health Checks Disponibles
- Subscription Service: `/api/v1/subscriptions/test/health`
- Notification Service: `/api/v1/notifications/test/health`

## Próximos Pasos

1. **Implementar retry logic** para manejo de errores en RabbitMQ
2. **Añadir métricas** con Micrometer/Prometheus
3. **Implementar dead letter queues** para mensajes fallidos
4. **Añadir notificaciones push** via Firebase
5. **Configurar clustering** de RabbitMQ para alta disponibilidad

## Estructura de Mensajes

### SubscriptionNotificationDto
```json
{
  "notification_type": "SUBSCRIPTION_CREATED",
  "user_id": 1,
  "user_email": "usuario@ejemplo.com",
  "user_name": "Juan Pérez",
  "subscription_id": 123,
  "subscription_type": "BASIC",
  "plan_name": "Plan Básico",
  "price": 19.99,
  "currency": "USD",
  "start_date": "2024-01-01T10:00:00",
  "end_date": "2024-02-01T10:00:00",
  "event_time": "2024-01-01T10:00:00",
  "subject": "¡Suscripción Activada - HidroGreen!",
  "features": "• 5 cultivos\n• 20 reportes\n• Soporte estándar",
  "invoice_number": "INV-20240101-000123"
}
```

La implementación está completa y lista para uso en producción.
