# Build stage
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom files
COPY pom.xml .
COPY ../pom.xml ../pom.xml

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM gcr.io/distroless/java21-debian12:latest

WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8083/actuator/health || exit 1

# Run the application
CMD ["app.jar"]
