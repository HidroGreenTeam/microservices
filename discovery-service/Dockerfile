# Dockerfile para Discovery Service - Multi-stage build
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar archivos de configuración
COPY pom.xml .

# Descargar dependencias (para cache de Docker)
RUN mvn dependency:go-offline

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR desde la imagen de build
COPY --from=build /app/target/discovery-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]
