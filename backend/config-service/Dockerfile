# Build stage
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY ../pom.xml ../pom.xml
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM gcr.io/distroless/java21-debian12:latest
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8888/actuator/health || exit 1
CMD ["app.jar"]
