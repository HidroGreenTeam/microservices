# HidroGreen - Sistema de Microservicios

## Descripción

HidroGreen es un sistema basado en microservicios diseñado para gestionar operaciones relacionadas con agricultura hidropónica. La arquitectura está diseñada usando Spring Cloud y sigue los principios de arquitectura de microservicios.

## Estructura del Proyecto

El proyecto está organizado en los siguientes microservicios:

### 1. Discovery Service

Servicio de descubrimiento basado en Netflix Eureka que permite el registro y localización de servicios.

- Puerto: 8761
- Endpoint: http://localhost:8761

### 2. API Gateway

Punto de entrada centralizado que enruta las peticiones a los microservicios correspondientes utilizando Spring Cloud Gateway.

- Puerto: 8080
- Rutas configuradas:
  - `/api/v1/auth/**` → user-service
  - `/api/v1/users/**` → user-service
  - `/api/v1/farmers/**` → user-service
  - `/api/v1/roles/**` → user-service

### 3. User Service

Servicio que gestiona la autenticación, perfiles de usuarios y roles.

- Funcionalidades:
  - Gestión de autenticación (IAM)
  - Gestión de perfiles de usuario
  - Gestión de roles y permisos

## Tecnologías Utilizadas

- **Spring Boot**: Framework para el desarrollo de aplicaciones Java
- **Spring Cloud**: Conjunto de herramientas para desarrollo de microservicios
- **Netflix Eureka**: Servicio de descubrimiento y registro
- **Spring Cloud Gateway**: API Gateway para enrutamiento de peticiones
- **Spring WebFlux**: Programación reactiva para servicios web

## Requisitos Previos

- Java 17 o superior
- Maven 3.8 o superior

## Cómo Ejecutar el Proyecto

### Usando Maven

1. Iniciar el servicio de descubrimiento (Discovery Service):

```bash
cd discovery-service
./mvnw spring-boot:run
```

2. Iniciar el servicio de usuarios (User Service):

```bash
cd user-service
./mvnw spring-boot:run
```

3. Iniciar el API Gateway:

```bash
cd api-gateway
./mvnw spring-boot:run
```

### Usando PowerShell

1. Iniciar el servicio de descubrimiento (Discovery Service):

```powershell
cd .\discovery-service\
.\mvnw.cmd spring-boot:run
```

2. Iniciar el servicio de usuarios (User Service):

```powershell
cd .\user-service\
.\mvnw.cmd spring-boot:run
```

3. Iniciar el API Gateway:

```powershell
cd .\api-gateway\
.\mvnw.cmd spring-boot:run
```

## Verificación del Despliegue

1. Acceder al panel de Eureka: http://localhost:8761
2. Verificar que todos los servicios están registrados correctamente
3. Probar el API Gateway accediendo a: http://localhost:8080/api/v1/users/

## Estructura de Código

La estructura de código sigue un patrón de arquitectura limpia con las siguientes capas:

- **application**: Lógica de negocio y casos de uso
- **domain**: Entidades y reglas de negocio centrales
- **infrastructure**: Implementaciones concretas (repositorios, servicios externos)
- **interfaces**: Controladores API y DTOs

## Contribución

Para contribuir al proyecto:

1. Crea un fork del repositorio
2. Crea una rama para tu funcionalidad (`git checkout -b feature/amazing-feature`)
3. Haz commit de tus cambios (`git commit -m 'Add some amazing feature'`)
4. Haz push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## Licencia

[Especificar la licencia del proyecto]

## Contacto

[Información de contacto del equipo/mantenedor]
