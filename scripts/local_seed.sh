#!/bin/bash

# VoxCare Local Data Seeding Script
# This script populates the system with demo data via API calls

set -e

echo "🌱 Seeding VoxCare with demo data..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to log messages
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

log_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} ERROR: $1"
}

log_warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} WARNING: $1"
}

# Check if services are running
check_service() {
    local service_name=$1
    local health_url=$2
    
    if curl -s -f "$health_url" > /dev/null 2>&1; then
        log "$service_name is running"
        return 0
    else
        log_error "$service_name is not running at $health_url"
        return 1
    fi
}

# Wait for service to be ready
wait_for_service() {
    local service_name=$1
    local health_url=$2
    local max_attempts=30
    local attempt=1
    
    log "Waiting for $service_name to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$health_url" > /dev/null 2>&1; then
            log "$service_name is ready!"
            return 0
        fi
        
        log "Attempt $attempt/$max_attempts - $service_name not ready yet..."
        sleep 5
        attempt=$((attempt + 1))
    done
    
    log_error "$service_name failed to start after $max_attempts attempts"
    return 1
}

# Check if required services are running
log "Checking service availability..."

if ! check_service "API Gateway" "http://localhost:8080/actuator/health"; then
    log_error "API Gateway is not running. Please start services with 'make up' first."
    exit 1
fi

if ! check_service "Auth Service" "http://localhost:8081/actuator/health"; then
    log_error "Auth Service is not running. Please start services with 'make up' first."
    exit 1
fi

log "All required services are running!"

# Function to make API calls
api_call() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    
    local headers="Content-Type: application/json"
    if [ -n "$token" ]; then
        headers="$headers Authorization: Bearer $token"
    fi
    
    if [ -n "$data" ]; then
        curl -s -X "$method" \
            -H "$headers" \
            -d "$data" \
            "http://localhost:8080$endpoint"
    else
        curl -s -X "$method" \
            -H "$headers" \
            "http://localhost:8080$endpoint"
    fi
}

# Function to extract JWT token from response
extract_token() {
    local response=$1
    echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4
}

# Function to extract refresh token from response
extract_refresh_token() {
    local response=$1
    echo "$response" | grep -o '"refreshToken":"[^"]*"' | cut -d'"' -f4
}

# Login functions
login_admin() {
    log "Logging in as admin..."
    local response=$(api_call "POST" "/api/auth/login" '{"email":"admin@demo.dev","password":"Passw0rd!"}')
    local token=$(extract_token "$response")
    
    if [ -n "$token" ]; then
        log "Admin login successful"
        echo "$token"
    else
        log_error "Admin login failed: $response"
        return 1
    fi
}

login_provider() {
    log "Logging in as provider..."
    local response=$(api_call "POST" "/api/auth/login" '{"email":"provider1@demo.dev","password":"Passw0rd!"}')
    local token=$(extract_token "$response")
    
    if [ -n "$token" ]; then
        log "Provider login successful"
        echo "$token"
    else
        log_error "Provider login failed: $response"
        return 1
    fi
}

login_patient() {
    log "Logging in as patient..."
    local response=$(api_call "POST" "/api/auth/login" '{"email":"patient1@demo.dev","password":"Passw0rd!"}')
    local token=$(extract_token "$response")
    
    if [ -n "$token" ]; then
        log "Patient login successful"
        echo "$token"
    else
        log_error "Patient login failed: $response"
        return 1
    fi
}

# Seed data functions
seed_provider_availability() {
    local token=$1
    log "Seeding provider availability..."
    
    # Create availability slots for next week
    local next_week=$(date -d "+7 days" +%Y-%m-%d)
    
    # Morning slots
    for hour in 9 10 11; do
        local start_time="${next_week}T${hour}:00:00"
        local end_time="${next_week}T$((hour+1)):00:00"
        
        local response=$(api_call "POST" "/api/providers/availability" \
            "{\"startTime\":\"$start_time\",\"endTime\":\"$end_time\",\"status\":\"AVAILABLE\"}" \
            "$token")
        
        if echo "$response" | grep -q "id"; then
            log "Created availability slot: $start_time - $end_time"
        else
            log_warning "Failed to create availability slot: $response"
        fi
    done
    
    # Afternoon slots
    for hour in 14 15 16; do
        local start_time="${next_week}T${hour}:00:00"
        local end_time="${next_week}T$((hour+1)):00:00"
        
        local response=$(api_call "POST" "/api/providers/availability" \
            "{\"startTime\":\"$start_time\",\"endTime\":\"$end_time\",\"status\":\"AVAILABLE\"}" \
            "$token")
        
        if echo "$response" | grep -q "id"; then
            log "Created availability slot: $start_time - $end_time"
        else
            log_warning "Failed to create availability slot: $response"
        fi
    done
}

seed_patient_appointment() {
    local token=$1
    log "Seeding patient appointment..."
    
    # Get available slots
    local next_week=$(date -d "+7 days" +%Y-%m-%d)
    local start_time="${next_week}T09:00:00"
    local end_time="${next_week}T10:00:00"
    
    # Book appointment
    local response=$(api_call "POST" "/api/appointments" \
        "{\"providerId\":1,\"startTime\":\"$start_time\",\"endTime\":\"$end_time\",\"notes\":\"Demo appointment\"}" \
        "$token")
    
    if echo "$response" | grep -q "id"; then
        log "Created demo appointment: $start_time - $end_time"
    else
        log_warning "Failed to create demo appointment: $response"
    fi
}

# Main seeding process
main() {
    log "Starting data seeding process..."
    
    # Login as different users
    local admin_token=$(login_admin)
    local provider_token=$(login_provider)
    local patient_token=$(login_patient)
    
    if [ -z "$admin_token" ] || [ -z "$provider_token" ] || [ -z "$patient_token" ]; then
        log_error "Failed to obtain authentication tokens"
        exit 1
    fi
    
    # Seed provider availability
    seed_provider_availability "$provider_token"
    
    # Seed patient appointment
    seed_patient_appointment "$patient_token"
    
    log "Data seeding completed successfully!"
    
    # Display summary
    echo ""
    echo "🎉 Demo data has been seeded!"
    echo ""
    echo "📅 Created availability slots for next week"
    echo "📋 Created demo appointment"
    echo ""
    echo "🔑 You can now log in with:"
    echo "   Admin:    admin@demo.dev / Passw0rd!"
    echo "   Provider: provider1@demo.dev / Passw0rd!"
    echo "   Patient:  patient1@demo.dev / Passw0rd!"
    echo ""
    echo "🌐 Access your applications:"
    echo "   Staff Portal:     http://localhost:3000"
    echo "   Patient Portal:   http://localhost:3001"
    echo "   API Gateway:      http://localhost:8080"
    echo "   Swagger UI:       http://localhost:8080/swagger-ui.html"
}

# Run main function
main "$@"
