#!/bin/bash

# PulseCare GitHub Repository Initialization Script
# This script creates a new GitHub repository and pushes the initial code

set -e

echo "🚀 Initializing PulseCare GitHub Repository..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
REPO_NAME="pulsecare-telehealth"
REPO_DESCRIPTION="Production-grade, horizontally scalable telehealth platform built with Spring Boot microservices, React/Vue frontends, and Kubernetes deployment"
REPO_TOPICS="spring-boot,microservices,react,vue,kubernetes,gke,healthcare,telehealth,java,spring-cloud,docker,postgresql,mongodb"

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

# Check if GitHub CLI is installed and authenticated
check_github_cli() {
    log "Checking GitHub CLI..."
    
    if ! command -v gh &> /dev/null; then
        log_error "GitHub CLI (gh) is not installed"
        log_info "Please install it from: https://cli.github.com/"
        log_info "Or run: brew install gh (on macOS)"
        exit 1
    fi
    
    # Check if authenticated
    if ! gh auth status &> /dev/null; then
        log_warning "GitHub CLI is not authenticated"
        log_info "Please run: gh auth login"
        log_info "Then run this script again"
        exit 1
    fi
    
    log_info "GitHub CLI is authenticated"
}

# Get GitHub username
get_github_username() {
    local username=$(gh api user --jq .login)
    if [ -z "$username" ]; then
        log_error "Could not determine GitHub username"
        exit 1
    fi
    echo "$username"
}

# Create GitHub repository
create_repository() {
    local username=$1
    
    log "Creating GitHub repository..."
    
    # Check if repository already exists
    if gh repo view "$username/$REPO_NAME" &> /dev/null; then
        log_warning "Repository $username/$REPO_NAME already exists"
        read -p "Do you want to continue with existing repository? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "Aborting repository creation"
            exit 0
        fi
        return 0
    fi
    
    # Create repository
    gh repo create "$username/$REPO_NAME" \
        --description "$REPO_DESCRIPTION" \
        --public \
        --source . \
        --remote origin \
        --push
    
    log_info "Repository created successfully: https://github.com/$username/$REPO_NAME"
}

# Add topics to repository
add_repository_topics() {
    local username=$1
    
    log "Adding repository topics..."
    
    gh repo edit "$username/$REPO_NAME" --add-topic $REPO_TOPICS
    
    log_info "Repository topics added: $REPO_TOPICS"
}

# Setup Git configuration
setup_git() {
    log "Setting up Git configuration..."
    
    # Check if git is initialized
    if [ ! -d ".git" ]; then
        log_info "Initializing Git repository..."
        git init
    fi
    
    # Add all files
    git add .
    
    # Create initial commit
    if ! git diff --cached --quiet; then
        git commit -m "Initial commit: PulseCare Telehealth Platform

🚀 Production-grade telehealth platform with:
- Spring Boot 3 microservices architecture
- React (Staff Portal) + Vue (Patient Portal) frontends
- Kubernetes deployment with HPA scaling
- PostgreSQL + MongoDB data layer
- JWT authentication and security
- Docker Compose for local development
- Comprehensive CI/CD pipeline

🔧 Features:
- Patient appointment booking and management
- Provider availability and telehealth sessions
- Secure Jitsi integration
- HIPAA-friendly design
- Horizontal scaling with Kubernetes HPA
- Monitoring and observability

📚 Documentation:
- Quick start guide
- Architecture diagrams
- API documentation
- Deployment instructions
- Demo scripts"
        
        log_info "Initial commit created"
    else
        log_info "No changes to commit"
    fi
    
    # Push to GitHub
    if git remote get-url origin &> /dev/null; then
        log_info "Pushing to GitHub..."
        git push -u origin main
        log_info "Code pushed to GitHub successfully"
    else
        log_warning "No remote origin configured"
    fi
}

# Create GitHub repository settings
setup_repository_settings() {
    local username=$1
    
    log "Setting up repository settings..."
    
    # Enable GitHub Actions
    gh repo edit "$username/$REPO_NAME" --enable-actions
    
    # Enable Dependabot alerts
    gh repo edit "$username/$REPO_NAME" --enable-vulnerability-alerts
    
    # Enable security policy
    gh repo edit "$username/$REPO_NAME" --enable-security-policy
    
    log_info "Repository settings configured"
}

# Create repository files
create_repository_files() {
    log "Creating repository files..."
    
    # Create .github/ISSUE_TEMPLATE.md
    mkdir -p .github
    cat > .github/ISSUE_TEMPLATE.md << 'EOF'
# Issue Template

## Description
Brief description of the issue or feature request.

## Type
- [ ] Bug report
- [ ] Feature request
- [ ] Documentation improvement
- [ ] Performance issue
- [ ] Security concern

## Environment
- OS: [e.g., macOS, Ubuntu, Windows]
- Java Version: [e.g., 21]
- Node.js Version: [e.g., 18.x]
- Docker Version: [e.g., 24.0]

## Steps to reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected behavior
What you expected to happen.

## Actual behavior
What actually happened.

## Additional context
Any other context about the issue.
EOF

    # Create .github/PULL_REQUEST_TEMPLATE.md
    cat > .github/PULL_REQUEST_TEMPLATE.md << 'EOF'
# Pull Request Template

## Description
Brief description of the changes.

## Type of change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] E2E tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Code is documented
- [ ] No new warnings introduced
- [ ] Tests added for new functionality

## Screenshots (if applicable)
Add screenshots to help explain your changes.

## Additional notes
Any additional information.
EOF

    # Create SECURITY.md
    cat > SECURITY.md << 'EOF'
# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability, please:

1. **Do NOT create a public GitHub issue**
2. Email security details to: [your-email@domain.com]
3. Include "SECURITY VULNERABILITY" in the subject line
4. Provide detailed description of the vulnerability
5. Include steps to reproduce if possible

## Security Features

- JWT-based authentication with short TTL
- BCrypt password hashing
- Role-based access control (RBAC)
- Input validation and sanitization
- HTTPS enforcement
- CORS protection
- SQL injection prevention
- XSS protection

## Security Best Practices

- Keep dependencies updated
- Use strong, unique passwords
- Enable 2FA on GitHub accounts
- Review code changes before merging
- Regular security audits
- Monitor for suspicious activity

## Compliance

This platform is designed to be HIPAA-friendly but is not production HIPAA compliant without additional controls and business associate agreements.
EOF

    # Create CONTRIBUTING.md
    cat > CONTRIBUTING.md << 'EOF'
# Contributing to PulseCare

Thank you for your interest in contributing to PulseCare Telehealth Platform!

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your-username/pulsecare-telehealth.git`
3. Create a feature branch: `git checkout -b feature/amazing-feature`
4. Make your changes
5. Test your changes: `make test`
6. Commit your changes: `git commit -m 'Add amazing feature'`
7. Push to your branch: `git push origin feature/amazing-feature`
8. Open a Pull Request

## Development Setup

### Prerequisites
- Java 21
- Node.js 18+
- Docker & Docker Compose
- Maven 3.9+

### Local Development
```bash
# Clone and setup
git clone <your-fork-url>
cd pulsecare-telehealth
make init

# Start services
make up

# Run tests
make test

# Build
make build
```

## Code Style

### Backend (Java)
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Add Javadoc for public methods
- Keep methods under 20 lines when possible
- Use meaningful commit messages

### Frontend (React/Vue)
- Follow project ESLint/Prettier configuration
- Use functional components (React)
- Use Composition API (Vue 3)
- Keep components focused and reusable

## Testing

- Write unit tests for new functionality
- Ensure all tests pass before submitting PR
- Add integration tests for API endpoints
- Include E2E tests for critical user flows

## Pull Request Guidelines

- Provide clear description of changes
- Include screenshots for UI changes
- Reference related issues
- Ensure CI/CD pipeline passes
- Request reviews from maintainers

## Questions?

Feel free to open an issue for questions or discussions.
EOF

    log_info "Repository files created"
}

# Main function
main() {
    log "🚀 Starting PulseCare GitHub Repository Setup"
    
    # Check GitHub CLI
    check_github_cli
    
    # Get GitHub username
    local username=$(get_github_username)
    log_info "GitHub username: $username"
    
    # Create repository files
    create_repository_files
    
    # Setup Git
    setup_git
    
    # Create GitHub repository
    create_repository "$username"
    
    # Add topics
    add_repository_topics "$username"
    
    # Setup repository settings
    setup_repository_settings "$username"
    
    # Final push
    log_info "Pushing final changes..."
    git add .
    git commit -m "Add repository templates and documentation" || true
    git push origin main || true
    
    log "🎉 Repository setup completed successfully!"
    log ""
    log "🌐 Your repository is available at: https://github.com/$username/$REPO_NAME"
    log ""
    log "📋 Next steps:"
    log "1. Review the repository settings"
    log "2. Set up branch protection rules"
    log "3. Configure required status checks"
    log "4. Add team members and collaborators"
    log "5. Set up monitoring and alerts"
    log ""
    log "🔧 Development commands:"
    log "  make init      - Initialize project"
    log "  make up        - Start services"
    log "  make test      - Run tests"
    log "  make build     - Build project"
    log "  make deploy    - Deploy to GKE"
}

# Run main function
main "$@"
