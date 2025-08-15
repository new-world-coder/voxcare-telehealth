#!/bin/bash

# PulseCare GCP Login and Setup Script
# This script authenticates with Google Cloud and sets up the project

set -e

echo "☁️  Setting up Google Cloud Platform for PulseCare..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Check if gcloud CLI is installed
check_gcloud() {
    log "Checking Google Cloud CLI..."
    
    if ! command -v gcloud &> /dev/null; then
        log_error "Google Cloud CLI (gcloud) is not installed"
        log_info "Please install it from: https://cloud.google.com/sdk/docs/install"
        log_info "Or run: brew install google-cloud-sdk (on macOS)"
        exit 1
    fi
    
    log_info "Google Cloud CLI is installed"
}

# Check if kubectl is installed
check_kubectl() {
    log "Checking kubectl..."
    
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl is not installed"
        log_info "Please install it from: https://kubernetes.io/docs/tasks/tools/"
        log_info "Or run: brew install kubectl (on macOS)"
        exit 1
    fi
    
    log_info "kubectl is installed"
}

# Authenticate with Google Cloud
authenticate_gcp() {
    log "Authenticating with Google Cloud..."
    
    # Check if already authenticated
    if gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
        log_info "Already authenticated with Google Cloud"
        local account=$(gcloud auth list --filter=status:ACTIVE --format="value(account)" | head -1)
        log_info "Active account: $account"
        return 0
    fi
    
    # Authenticate
    log_info "Please authenticate with Google Cloud..."
    gcloud auth login
    
    if [ $? -eq 0 ]; then
        log_info "Authentication successful"
    else
        log_error "Authentication failed"
        exit 1
    fi
}

# Set up application default credentials
setup_adc() {
    log "Setting up Application Default Credentials..."
    
    gcloud auth application-default login
    
    if [ $? -eq 0 ]; then
        log_info "Application Default Credentials set up successfully"
    else
        log_error "Failed to set up Application Default Credentials"
        exit 1
    fi
}

# List available projects
list_projects() {
    log "Available Google Cloud projects:"
    
    gcloud projects list --format="table(projectId,name,projectNumber)"
}

# Set project
set_project() {
    local project_id=$1
    
    if [ -z "$project_id" ]; then
        log_info "Please enter your Google Cloud Project ID:"
        read -p "Project ID: " project_id
        
        if [ -z "$project_id" ]; then
            log_error "Project ID is required"
            exit 1
        fi
    fi
    
    log "Setting project to: $project_id"
    
    gcloud config set project "$project_id"
    
    if [ $? -eq 0 ]; then
        log_info "Project set successfully"
        
        # Verify project
        local current_project=$(gcloud config get-value project)
        if [ "$current_project" = "$project_id" ]; then
            log_info "Current project: $current_project"
        else
            log_error "Failed to set project"
            exit 1
        fi
    else
        log_error "Failed to set project"
        exit 1
    fi
}

# Enable required APIs
enable_apis() {
    local project_id=$(gcloud config get-value project)
    
    log "Enabling required Google Cloud APIs for project: $project_id"
    
    # Enable Container Registry API
    log_info "Enabling Container Registry API..."
    gcloud services enable containerregistry.googleapis.com
    
    # Enable Kubernetes Engine API
    log_info "Enabling Kubernetes Engine API..."
    gcloud services enable container.googleapis.com
    
    # Enable Compute Engine API
    log_info "Enabling Compute Engine API..."
    gcloud services enable compute.googleapis.com
    
    # Enable Cloud Build API
    log_info "Enabling Cloud Build API..."
    gcloud services enable cloudbuild.googleapis.com
    
    # Enable IAM API
    log_info "Enabling IAM API..."
    gcloud services enable iam.googleapis.com
    
    # Enable Resource Manager API
    log_info "Enabling Resource Manager API..."
    gcloud services enable cloudresourcemanager.googleapis.com
    
    log_info "All required APIs enabled"
}

# Configure Docker for GCR
configure_docker() {
    log "Configuring Docker for Google Container Registry..."
    
    gcloud auth configure-docker
    
    if [ $? -eq 0 ]; then
        log_info "Docker configured for GCR successfully"
    else
        log_error "Failed to configure Docker for GCR"
        exit 1
    fi
}

# Create service account for CI/CD
create_service_account() {
    local project_id=$(gcloud config get-value project)
    local sa_name="pulsecare-ci-cd"
    local sa_email="$sa_name@$project_id.iam.gserviceaccount.com"
    
    log "Creating service account for CI/CD: $sa_name"
    
    # Check if service account already exists
    if gcloud iam service-accounts describe "$sa_email" &> /dev/null; then
        log_info "Service account already exists: $sa_email"
        return 0
    fi
    
    # Create service account
    gcloud iam service-accounts create "$sa_name" \
        --display-name="PulseCare CI/CD Service Account" \
        --description="Service account for PulseCare CI/CD pipeline"
    
    if [ $? -eq 0 ]; then
        log_info "Service account created successfully"
    else
        log_error "Failed to create service account"
        exit 1
    fi
    
    # Grant required roles
    log_info "Granting required roles to service account..."
    
    # Container Registry Admin
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$sa_email" \
        --role="roles/storage.admin"
    
    # Kubernetes Engine Admin
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$sa_email" \
        --role="roles/container.admin"
    
    # Cloud Build Service Account
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$sa_email" \
        --role="roles/cloudbuild.builds.builder"
    
    # IAM Service Account User
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$sa_email" \
        --role="roles/iam.serviceAccountUser"
    
    log_info "Roles granted successfully"
    
    # Create and download key
    log_info "Creating service account key..."
    
    local key_file="pulsecare-ci-cd-key.json"
    gcloud iam service-accounts keys create "$key_file" \
        --iam-account="$sa_email"
    
    if [ $? -eq 0 ]; then
        log_info "Service account key created: $key_file"
        log_warning "Keep this key secure and add it to GitHub Secrets as GCP_SA_KEY"
    else
        log_error "Failed to create service account key"
        exit 1
    fi
}

# Set up default compute region and zone
setup_compute() {
    log "Setting up default compute region and zone..."
    
    # Set default region
    local region="us-central1"
    log_info "Setting default region to: $region"
    gcloud config set compute/region "$region"
    
    # Set default zone
    local zone="us-central1-a"
    log_info "Setting default zone to: $zone"
    gcloud config set compute/zone "$zone"
    
    log_info "Compute defaults configured"
}

# Display configuration summary
show_config() {
    log "Google Cloud Configuration Summary:"
    echo ""
    
    local project_id=$(gcloud config get-value project)
    local account=$(gcloud auth list --filter=status:ACTIVE --format="value(account)" | head -1)
    local region=$(gcloud config get-value compute/region)
    local zone=$(gcloud config get-value compute/zone)
    
    echo "Project ID: $project_id"
    echo "Account: $account"
    echo "Region: $region"
    echo "Zone: $zone"
    echo ""
    
    log_info "Configuration saved to: ~/.config/gcloud/configurations/config_default"
}

# Main function
main() {
    log "☁️  Starting Google Cloud Platform Setup for PulseCare"
    
    # Check dependencies
    check_gcloud
    check_kubectl
    
    # Authenticate
    authenticate_gcp
    
    # Set up ADC
    setup_adc
    
    # List projects
    list_projects
    
    # Set project
    set_project "$1"
    
    # Enable APIs
    enable_apis
    
    # Configure Docker
    configure_docker
    
    # Create service account
    create_service_account
    
    # Set up compute defaults
    setup_compute
    
    # Show configuration
    show_config
    
    log "🎉 Google Cloud Platform setup completed successfully!"
    log ""
    log "📋 Next steps:"
    log "1. Add the service account key to GitHub Secrets as GCP_SA_KEY"
    log "2. Set GCP_PROJECT_ID in GitHub Secrets: $(gcloud config get-value project)"
    log "3. Set GKE_CLUSTER in GitHub Secrets: pulsecare-cluster"
    log "4. Set GKE_ZONE in GitHub Secrets: $(gcloud config get-value compute/zone)"
    log "5. Run: ./scripts/gke_deploy.sh to deploy to GKE"
    log ""
    log "🔑 Service account key file: pulsecare-ci-cd-key.json"
    log "⚠️  Keep this file secure and do not commit it to version control"
}

# Run main function
main "$@"
