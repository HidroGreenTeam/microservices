# Subscription Service

## Descripción General

Este servicio es responsable de gestionar las suscripciones de los usuarios, incluyendo la creación, cancelación y renovación de las mismas.

## Comunicación con Otros Servicios

El `subscription-service` interactúa con otros microservicios utilizando dos patrones de comunicación principales:

### 1. User Service (Comunicación Síncrona)

- **Patrón:** Comunicación Directa HTTP/REST (Síncrona)
- **Mecanismo:** Utiliza un `RestTemplate` de Spring para realizar llamadas directas al `user-service`.
- **Endpoint:** Obtiene los datos del usuario realizando una petición `GET` a la URL `http://user-service:8081/api/v1/users/{userId}`.
- **Propósito:** Recuperar detalles esenciales del usuario (como email y nombre) que son necesarios para la gestión de suscripciones. Al ser una llamada síncrona, el `subscription-service` espera una respuesta del `user-service` antes de continuar con su proceso.

### 2. Notification Service (Comunicación Asíncrona)

- **Patrón:** Comunicación Orientada a Eventos a través de un Message Broker (Asíncrona)
- **Mecanismo:** Utiliza RabbitMQ para publicar eventos relacionados con cambios en el estado de las suscripciones. Esto desacopla al `subscription-service` del `notification-service`.
- **Detalles de la Configuración de RabbitMQ:**
    - **Exchange:** Se utiliza un Topic Exchange llamado `subscription.exchange`.
    - **Cola:** Los mensajes se envían a la cola `subscription.notification.queue`, que es consumida por el `notification-service`.
    - **Eventos Publicados (Routing Keys):**
        - `subscription.created`: Se publica cuando se crea una nueva suscripción.
        - `subscription.cancelled`: Se publica cuando se cancela una suscripción.
        - `subscription.renewed`: Se publica cuando se renueva una suscripción.
- **Propósito:** Notificar a otras partes del sistema sobre cambios en las suscripciones sin necesidad de esperar una respuesta directa. El `notification-service` escucha estos eventos y se encarga de enviar las notificaciones correspondientes (por ejemplo, por email) al usuario final.

Esta combinación de comunicación síncrona y asíncrona permite al servicio obtener datos críticos de forma inmediata, mientras que delega tareas menos urgentes como el envío de notificaciones a un proceso asíncrono, mejorando así la resiliencia y escalabilidad del sistema.
