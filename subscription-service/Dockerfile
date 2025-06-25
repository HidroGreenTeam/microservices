# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file
COPY target/subscription-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8087

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
