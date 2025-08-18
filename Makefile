.PHONY: help init build test clean up down swagger seed load-test security

# Default target
help:
	@echo "PulseCare Telehealth Scheduler - Available Commands:"
	@echo ""
	@echo "Setup:"
	@echo "  init        - Initialize project (Maven wrapper, Node modules, pre-commit)"
	@echo "  build       - Build all backend and frontend components"
	@echo ""
	@echo "Development:"
	@echo "  up          - Start all services with Docker Compose"
	@echo "  down        - Stop all services"
	@echo "  logs        - View logs for all services"
	@echo "  seed        - Seed demo data"
	@echo ""
	@echo "Testing:"
	@echo "  test        - Run all tests (backend + frontend + e2e)"
	@echo "  test-backend - Run backend tests only"
	@echo "  test-frontend - Run frontend tests only"
	@echo "  test-e2e    - Run end-to-end tests"
	@echo ""
	@echo "Utilities:"
	@echo "  swagger     - Export OpenAPI specifications"
	@echo "  clean       - Clean build artifacts"
	@echo "  load-test   - Run load testing to trigger HPA scaling"
	@echo ""
	@echo "Security:"
	@echo "  security    - Run comprehensive security analysis and compliance checks"
	@echo ""

# Initialize project
init:
	@echo "🚀 Initializing PulseCare project..."
	@if [ ! -f "backend/pom.xml" ]; then echo "❌ Backend not found. Please ensure you're in the correct directory."; exit 1; fi
	
	# Install Maven wrapper if not present
	@if [ ! -f "backend/.mvn/wrapper/maven-wrapper.jar" ]; then \
		echo "📦 Installing Maven wrapper..."; \
		cd backend && mvn wrapper:wrapper; \
	fi
	
	# Install Node modules for frontends
	@echo "📦 Installing Node modules..."
	@if [ -d "frontend/staff-portal-react" ]; then \
		cd frontend/staff-portal-react && npm install; \
	fi
	@if [ -d "frontend/patient-portal-vue" ]; then \
		cd frontend/patient-portal-vue && npm install; \
	fi
	
	# Install pre-commit hooks
	@echo "🔧 Installing pre-commit hooks..."
	@if command -v pre-commit >/dev/null 2>&1; then \
		pre-commit install; \
	else \
		echo "⚠️  pre-commit not found. Install with: pip install pre-commit"; \
	fi
	
	@echo "✅ Project initialization complete!"

# Build all components
build: build-backend build-frontend

# Build backend services
build-backend:
	@echo "🔨 Building backend services..."
	@cd backend && ./mvnw clean verify -DskipTests
	@echo "✅ Backend build complete!"

# Build frontend applications
build-frontend:
	@echo "🔨 Building frontend applications..."
	@if [ -d "frontend/staff-portal-react" ]; then \
		cd frontend/staff-portal-react && npm run build; \
	fi
	@if [ -d "frontend/patient-portal-vue" ]; then \
		cd frontend/patient-portal-vue && npm run build; \
	fi
	@echo "✅ Frontend build complete!"

# Start all services
up:
	@echo "🚀 Starting PulseCare services..."
	@docker-compose up -d
	@echo "⏳ Waiting for services to be ready..."
	@./scripts/wait-for-services.sh
	@echo "✅ All services are running!"
	@echo ""
	@echo "🌐 Access your applications:"
	@echo "   Staff Portal:     http://localhost:3000"
	@echo "   Patient Portal:   http://localhost:3001"
	@echo "   API Gateway:      http://localhost:8080"
	@echo "   Eureka Dashboard: http://localhost:8761"
	@echo "   Swagger UI:       http://localhost:8080/swagger-ui.html"

# Stop all services
down:
	@echo "🛑 Stopping PulseCare services..."
	@docker-compose down
	@echo "✅ All services stopped!"

# View logs
logs:
	@docker-compose logs -f

# Seed demo data
seed:
	@echo "🌱 Seeding demo data..."
	@./scripts/local_seed.sh
	@echo "✅ Demo data seeded!"

# Run all tests
test: test-backend test-frontend test-e2e

# Run backend tests
test-backend:
	@echo "🧪 Running backend tests..."
	@cd backend && ./mvnw test
	@echo "✅ Backend tests complete!"

# Run frontend tests
test-frontend:
	@echo "🧪 Running frontend tests..."
	@if [ -d "frontend/staff-portal-react" ]; then \
		cd frontend/staff-portal-react && npm test; \
	fi
	@if [ -d "frontend/patient-portal-vue" ]; then \
		cd frontend/patient-portal-vue && npm test; \
	fi
	@echo "✅ Frontend tests complete!"

# Run end-to-end tests
test-e2e:
	@echo "🧪 Running end-to-end tests..."
	@if [ -d "frontend/staff-portal-react" ]; then \
		cd frontend/staff-portal-react && npm run test:e2e; \
	fi
	@if [ -d "frontend/patient-portal-vue" ]; then \
		cd frontend/patient-portal-vue && npm run test:e2e; \
	fi
	@echo "✅ E2E tests complete!"

# Export OpenAPI specifications
swagger:
	@echo "📚 Exporting OpenAPI specifications..."
	@mkdir -p docs/api
	@cd backend && ./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=export" &
	@sleep 30
	@curl -s http://localhost:8080/v3/api-docs > docs/api/gateway-openapi.json
	@curl -s http://localhost:8081/v3/api-docs > docs/api/auth-openapi.json
	@curl -s http://localhost:8082/v3/api-docs > docs/api/patient-openapi.json
	@curl -s http://localhost:8083/v3/api-docs > docs/api/provider-openapi.json
	@curl -s http://localhost:8084/v3/api-docs > docs/api/appointment-openapi.json
	@curl -s http://localhost:8085/v3/api-docs > docs/api/telehealth-openapi.json
	@curl -s http://localhost:8086/v3/api-docs > docs/api/notification-openapi.json
	@pkill -f "spring-boot:run"
	@echo "✅ OpenAPI specifications exported to docs/api/"

# Clean build artifacts
clean:
	@echo "🧹 Cleaning build artifacts..."
	@cd backend && ./mvnw clean
	@if [ -d "frontend/staff-portal-react" ]; then \
		cd frontend/staff-portal-react && rm -rf dist node_modules/.cache; \
	fi
	@if [ -d "frontend/patient-portal-vue" ]; then \
		cd frontend/patient-portal-vue && rm -rf dist node_modules/.cache; \
	fi
	@docker-compose down -v
	@docker system prune -f
	@echo "✅ Cleanup complete!"

# Run load testing
load-test:
	@echo "📊 Running load testing to trigger HPA scaling..."
	@./scripts/load_test_hpa.sh

# Health check
health:
	@echo "🏥 Checking service health..."
	@curl -s http://localhost:8080/actuator/health | jq . || echo "❌ API Gateway not responding"
	@curl -s http://localhost:8761/actuator/health | jq . || echo "❌ Eureka not responding"
	@curl -s http://localhost:8081/actuator/health | jq . || echo "❌ Auth Service not responding"

# Database status
db-status:
	@echo "🗄️  Checking database status..."
	@docker-compose exec postgres pg_isready -U pulsecare || echo "❌ PostgreSQL not ready"
	@docker-compose exec mongo mongosh --eval "db.adminCommand('ping')" --quiet || echo "❌ MongoDB not ready"

# Service status
status:
	@echo "📊 Service Status:"
	@docker-compose ps
	@echo ""
	@echo "🔍 Health Checks:"
	@make health
	@echo ""
	@echo "🗄️  Database Status:"
	@make db-status

# Security analysis and compliance checks
security:
	@echo "🔒 Running comprehensive security analysis..."
	@echo "This will check for HIPAA, PCI, HL7, GDPR, and OWASP compliance issues."
	@echo ""
	@./scripts/security-lint.sh
	@echo ""
	@echo "📊 Security analysis complete! Check the security-reports/ directory for detailed results."
