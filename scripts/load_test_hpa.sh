#!/bin/bash

# VoxCare Load Testing Script for HPA Scaling
# This script simulates traffic to trigger horizontal pod autoscaling

set -e

echo "📊 Starting VoxCare Load Testing for HPA Scaling..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL=${1:-"http://localhost:8080"}
DURATION=${2:-300}  # 5 minutes default
RATE=${3:-100}      # 100 requests per second default
CONCURRENT=${4:-10} # 10 concurrent users default

# Function to log messages
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

log_info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} INFO: $1"
}

log_warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} WARNING: $1"
}

log_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} ERROR: $1"
}

# Check if required tools are available
check_dependencies() {
    log "Checking dependencies..."
    
    if ! command -v curl &> /dev/null; then
        log_error "curl is required but not installed"
        exit 1
    fi
    
    if ! command -v kubectl &> /dev/null; then
        log_warning "kubectl not found - HPA monitoring will be limited"
    fi
    
    if ! command -v k6 &> /dev/null; then
        log_warning "k6 not found - using curl-based load testing"
        USE_K6=false
    else
        USE_K6=true
        log_info "k6 found - will use for advanced load testing"
    fi
}

# Check if services are running
check_services() {
    log "Checking service availability..."
    
    if ! curl -s -f "${BASE_URL}/actuator/health" > /dev/null; then
        log_error "API Gateway is not accessible at ${BASE_URL}"
        exit 1
    fi
    
    log_info "API Gateway is accessible"
}

# Get authentication token
get_auth_token() {
    log "Getting authentication token..."
    
    local response=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"email":"admin@demo.dev","password":"Passw0rd!"}')
    
    local token=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    
    if [ -z "$token" ]; then
        log_error "Failed to get authentication token"
        exit 1
    fi
    
    log_info "Authentication token obtained"
    echo "$token"
}

# Monitor HPA status
monitor_hpa() {
    if ! command -v kubectl &> /dev/null; then
        log_warning "kubectl not available - skipping HPA monitoring"
        return
    fi
    
    log_info "Monitoring HPA status..."
    
    # Get current HPA status
    kubectl get hpa appointment-service-hpa -n voxcare || true
    
    # Get current pod count
    local current_pods=$(kubectl get pods -n voxcare -l app=appointment-service --no-headers | wc -l)
    log_info "Current appointment-service pods: $current_pods"
    
    # Get HPA metrics
    kubectl top pods -n voxcare -l app=appointment-service || true
}

# Run k6 load test
run_k6_test() {
    local token=$1
    
    log "Running k6 load test..."
    
    cat > /tmp/voxcare-load-test.js << EOF
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
  stages: [
    { duration: '30s', target: 10 },   // Ramp up
    { duration: '${DURATION}s', target: ${CONCURRENT} }, // Stay at target
    { duration: '30s', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests should be below 2s
    errors: ['rate<0.1'],              // Error rate should be below 10%
  },
};

export default function() {
  const baseUrl = '${BASE_URL}';
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ${token}',
  };
  
  // Test appointment search
  const searchResponse = http.get(\`\${baseUrl}/api/appointments/search?date=\${new Date().toISOString().split('T')[0]}\`, { headers });
  check(searchResponse, { 'search status is 200': (r) => r.status === 200 });
  
  // Test availability search
  const availabilityResponse = http.get(\`\${baseUrl}/api/providers/availability?date=\${new Date().toISOString().split('T')[0]}\`, { headers });
  check(availabilityResponse, { 'availability status is 200': (r) => r.status === 200 });
  
  // Test patient appointments
  const appointmentsResponse = http.get(\`\${baseUrl}/api/appointments/patient\`, { headers });
  check(appointmentsResponse, { 'appointments status is 200': (r) => r.status === 200 });
  
  // Random sleep between requests
  sleep(Math.random() * 2 + 1);
}
EOF

    k6 run /tmp/voxcare-load-test.js
}

# Run curl-based load test
run_curl_test() {
    local token=$1
    
    log "Running curl-based load test..."
    
    local start_time=$(date +%s)
    local end_time=$((start_time + DURATION))
    local request_count=0
    
    log_info "Load testing for ${DURATION} seconds at ${RATE} requests/second"
    
    while [ $(date +%s) -lt $end_time ]; do
        # Make concurrent requests
        for i in $(seq 1 $CONCURRENT); do
            (
                # Test appointment search
                curl -s -X GET "${BASE_URL}/api/appointments/search?date=$(date +%Y-%m-%d)" \
                    -H "Authorization: Bearer ${token}" > /dev/null &
                
                # Test availability search
                curl -s -X GET "${BASE_URL}/api/providers/availability?date=$(date +%Y-%m-%d)" \
                    -H "Authorization: Bearer ${token}" > /dev/null &
                
                # Test patient appointments
                curl -s -X GET "${BASE_URL}/api/appointments/patient" \
                    -H "Authorization: Bearer ${token}" > /dev/null &
            ) &
        done
        
        wait
        request_count=$((request_count + CONCURRENT * 3))
        
        # Show progress
        local elapsed=$(( $(date +%s) - start_time ))
        local remaining=$(( DURATION - elapsed ))
        echo -ne "\r${BLUE}Progress: ${elapsed}s/${DURATION}s (${remaining}s remaining) - Requests: ${request_count}${NC}"
        
        # Rate limiting
        sleep $((1 / RATE))
    done
    
    echo ""
    log_info "Load testing completed. Total requests: ${request_count}"
}

# Main load testing function
main() {
    log "🚀 Starting VoxCare Load Testing"
    log "Base URL: ${BASE_URL}"
    log "Duration: ${DURATION} seconds"
    log "Rate: ${RATE} requests/second"
    log "Concurrent users: ${CONCURRENT}"
    
    # Check dependencies and services
    check_dependencies
    check_services
    
    # Get authentication token
    local token=$(get_auth_token)
    
    # Show initial HPA status
    log_info "Initial HPA status:"
    monitor_hpa
    
    # Start load testing
    log "Starting load test..."
    
    if [ "$USE_K6" = true ]; then
        run_k6_test "$token"
    else
        run_curl_test "$token"
    fi
    
    # Show final HPA status
    log_info "Final HPA status:"
    monitor_hpa
    
    # Show scaling results
    log "🎯 Load testing completed!"
    log "Check HPA scaling with: kubectl get hpa -n voxcare"
    log "Monitor pods with: kubectl get pods -n voxcare -l app=appointment-service"
    log "View HPA metrics with: kubectl top pods -n voxcare"
}

# Run main function
main "$@"
