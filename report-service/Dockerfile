# Dockerfile para Report Service - Multi-stage build
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/report-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]
