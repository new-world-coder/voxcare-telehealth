#!/bin/bash

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."

# Function to wait for a service to be healthy
wait_for_service() {
    local service_name=$1
    local health_url=$2
    local max_attempts=30
    local attempt=1
    
    echo "  Waiting for $service_name..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$health_url" > /dev/null 2>&1; then
            echo "  ✅ $service_name is ready!"
            return 0
        fi
        
        echo "    Attempt $attempt/$max_attempts - $service_name not ready yet..."
        sleep 10
        attempt=$((attempt + 1))
    done
    
    echo "  ❌ $service_name failed to start after $max_attempts attempts"
    return 1
}

# Wait for core infrastructure services
echo "🏗️  Waiting for infrastructure services..."

# Wait for PostgreSQL
if ! wait_for_service "PostgreSQL" "http://localhost:5432"; then
    echo "❌ PostgreSQL failed to start"
    exit 1
fi

# Wait for MongoDB
if ! wait_for_service "MongoDB" "http://localhost:27017"; then
    echo "❌ MongoDB failed to start"
    exit 1
fi

# Wait for Kafka (optional)
if docker-compose ps kafka | grep -q "Up"; then
    if ! wait_for_service "Kafka" "http://localhost:9092"; then
        echo "⚠️  Kafka failed to start (optional service)"
    fi
fi

echo "🏗️  Infrastructure services are ready!"

# Wait for microservices
echo "🔧 Waiting for microservices..."

# Wait for Discovery Service
if ! wait_for_service "Discovery Service" "http://localhost:8761/actuator/health"; then
    echo "❌ Discovery Service failed to start"
    exit 1
fi

# Wait for Config Service
if ! wait_for_service "Config Service" "http://localhost:8888/actuator/health"; then
    echo "❌ Config Service failed to start"
    exit 1
fi

# Wait for Auth Service
if ! wait_for_service "Auth Service" "http://localhost:8081/actuator/health"; then
    echo "❌ Auth Service failed to start"
    exit 1
fi

# Wait for Patient Service
if ! wait_for_service "Patient Service" "http://localhost:8082/actuator/health"; then
    echo "❌ Patient Service failed to start"
    exit 1
fi

# Wait for Provider Service
if ! wait_for_service "Provider Service" "http://localhost:8083/actuator/health"; then
    echo "❌ Provider Service failed to start"
    exit 1
fi

# Wait for Appointment Service
if ! wait_for_service "Appointment Service" "http://localhost:8084/actuator/health"; then
    echo "❌ Appointment Service failed to start"
    exit 1
fi

# Wait for Telehealth Service
if ! wait_for_service "Telehealth Service" "http://localhost:8085/actuator/health"; then
    echo "❌ Telehealth Service failed to start"
    exit 1
fi

# Wait for Notification Service
if ! wait_for_service "Notification Service" "http://localhost:8086/actuator/health"; then
    echo "❌ Notification Service failed to start"
    exit 1
fi

# Wait for API Gateway
if ! wait_for_service "API Gateway" "http://localhost:8080/actuator/health"; then
    echo "❌ API Gateway failed to start"
    exit 1
fi

echo "🔧 All microservices are ready!"

# Wait for frontend services
echo "🎨 Waiting for frontend services..."

# Wait for Staff Portal
if ! wait_for_service "Staff Portal" "http://localhost:3000"; then
    echo "❌ Staff Portal failed to start"
    exit 1
fi

# Wait for Patient Portal
if ! wait_for_service "Patient Portal" "http://localhost:3001"; then
    echo "❌ Patient Portal failed to start"
    exit 1
fi

echo "🎨 Frontend services are ready!"

# Final health check
echo "🏥 Performing final health check..."

# Check API Gateway health
echo "  Checking API Gateway..."
if curl -s "http://localhost:8080/actuator/health" | grep -q "UP"; then
    echo "  ✅ API Gateway is healthy"
else
    echo "  ❌ API Gateway health check failed"
    exit 1
fi

# Check Eureka dashboard
echo "  Checking Eureka Dashboard..."
if curl -s "http://localhost:8761" | grep -q "EUREKA"; then
    echo "  ✅ Eureka Dashboard is accessible"
else
    echo "  ❌ Eureka Dashboard check failed"
    exit 1
fi

echo "🎉 All services are ready and healthy!"
echo ""
echo "🌐 Access your applications:"
echo "   Staff Portal:     http://localhost:3000"
echo "   Patient Portal:   http://localhost:3001"
echo "   API Gateway:      http://localhost:8080"
echo "   Eureka Dashboard: http://localhost:8761"
echo "   Swagger UI:       http://localhost:8080/swagger-ui.html"
echo ""
echo "🔑 Demo credentials:"
echo "   Admin:    admin@demo.dev / Passw0rd!"
echo "   Provider: provider1@demo.dev / Passw0rd!"
echo "   Patient:  patient1@demo.dev / Passw0rd!"
