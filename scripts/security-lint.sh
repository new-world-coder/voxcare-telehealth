#!/bin/bash

# VoxCare Security Linting and Static Analysis Script
# HIPAA, PCI, HL7, GDPR, and OWASP Compliance Scanner

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORTS_DIR="$PROJECT_ROOT/security-reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create reports directory
mkdir -p "$REPORTS_DIR"

echo -e "${BLUE}🔒 VoxCare Security Linting and Static Analysis${NC}"
echo -e "${BLUE}================================================${NC}"
echo "Project: $PROJECT_ROOT"
echo "Timestamp: $TIMESTAMP"
echo "Reports: $REPORTS_DIR"
echo ""

# Function to log messages
log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Install required tools if not present
install_tools() {
    log "Checking and installing required security analysis tools..."
    
    # Check for Java tools
    if ! command_exists "mvn"; then
        error "Maven not found. Please install Maven first."
        exit 1
    fi
    
    # Check for Node.js tools
    if ! command_exists "npm"; then
        error "npm not found. Please install Node.js first."
        exit 1
    fi
    
    # Install SpotBugs if not present
    if ! command_exists "spotbugs"; then
        log "Installing SpotBugs..."
        if command_exists "brew"; then
            brew install spotbugs
        else
            warn "SpotBugs not found. Please install manually: https://spotbugs.github.io/"
        fi
    fi
    
    # Install OWASP Dependency Check if not present
    if ! command_exists "dependency-check"; then
        log "Installing OWASP Dependency Check..."
        if command_exists "brew"; then
            brew install dependency-check
        else
            warn "OWASP Dependency Check not found. Please install manually: https://owasp.org/www-project-dependency-check/"
        fi
    fi
    
    # Install Semgrep if not present
    if ! command_exists "semgrep"; then
        log "Installing Semgrep..."
        if command_exists "brew"; then
            brew install semgrep
        else
            pip3 install semgrep
        fi
    fi
}

# Java Security Analysis
analyze_java_security() {
    log "🔍 Analyzing Java security vulnerabilities..."
    
    cd "$PROJECT_ROOT/backend"
    
    # Run SpotBugs security analysis
    if command_exists "spotbugs"; then
        log "Running SpotBugs security analysis..."
        for service in */; do
            if [ -f "$service/pom.xml" ]; then
                service_name=$(basename "$service")
                log "Analyzing $service_name..."
                
                cd "$service"
                mvn clean compile
                
                # Run SpotBugs with security rules
                spotbugs -text -high -medium -low \
                    -include "$PROJECT_ROOT/scripts/spotbugs-security.xml" \
                    -output "$REPORTS_DIR/spotbugs-$service_name-$TIMESTAMP.txt" \
                    target/classes/ || true
                
                cd ..
            fi
        done
    fi
    
    # Run OWASP Dependency Check
    if command_exists "dependency-check"; then
        log "Running OWASP Dependency Check..."
        for service in */; do
            if [ -f "$service/pom.xml" ]; then
                service_name=$(basename "$service")
                log "Checking dependencies for $service_name..."
                
                dependency-check --scan "$service" \
                    --format "HTML" \
                    --format "JSON" \
                    --out "$REPORTS_DIR/dependency-check-$service_name-$TIMESTAMP" \
                    --suppression "$PROJECT_ROOT/scripts/dependency-check-suppressions.xml" || true
            fi
        done
    fi
    
    cd "$PROJECT_ROOT"
}

# Frontend Security Analysis
analyze_frontend_security() {
    log "🔍 Analyzing frontend security vulnerabilities..."
    
    # Vue.js Patient Portal
    if [ -d "$PROJECT_ROOT/frontend/patient-portal-vue" ]; then
        log "Analyzing Vue.js patient portal..."
        cd "$PROJECT_ROOT/frontend/patient-portal-vue"
        
        # Install dependencies if needed
        if [ ! -d "node_modules" ]; then
            npm install
        fi
        
        # Run security audit
        npm audit --audit-level moderate > "$REPORTS_DIR/npm-audit-vue-$TIMESTAMP.txt" 2>&1 || true
        
        # Run ESLint with security rules
        if [ -f "package.json" ]; then
            npm run lint > "$REPORTS_DIR/eslint-vue-$TIMESTAMP.txt" 2>&1 || true
        fi
        
        cd "$PROJECT_ROOT"
    fi
    
    # React Staff Portal
    if [ -d "$PROJECT_ROOT/frontend/staff-portal-react" ]; then
        log "Analyzing React staff portal..."
        cd "$PROJECT_ROOT/frontend/staff-portal-react"
        
        # Install dependencies if needed
        if [ ! -d "node_modules" ]; then
            npm install
        fi
        
        # Run security audit
        npm audit --audit-level moderate > "$REPORTS_DIR/npm-audit-react-$TIMESTAMP.txt" 2>&1 || true
        
        cd "$PROJECT_ROOT"
    fi
}

# Semgrep Security Analysis
run_semgrep_analysis() {
    log "🔍 Running Semgrep security analysis..."
    
    if command_exists "semgrep"; then
        # Run Semgrep with security rules
        semgrep scan \
            --config auto \
            --config p/security-audit \
            --config p/owasp-top-ten \
            --config p/hipaa \
            --json "$REPORTS_DIR/semgrep-$TIMESTAMP.json" \
            --html "$REPORTS_DIR/semgrep-$TIMESTAMP.html" \
            --txt "$REPORTS_DIR/semgrep-$TIMESTAMP.txt" \
            . || true
    else
        warn "Semgrep not available. Skipping Semgrep analysis."
    fi
}

# Custom Security Rules Analysis
run_custom_security_analysis() {
    log "🔍 Running custom security rules analysis..."
    
    # HIPAA Compliance Checks
    log "Checking HIPAA compliance..."
    
    # Check for PHI in logs
    grep -r --include="*.java" --include="*.js" --include="*.vue" --include="*.tsx" \
        -E "(ssn|social.*security|patient.*id|medical.*record)" \
        "$PROJECT_ROOT" > "$REPORTS_DIR/hipaa-phi-check-$TIMESTAMP.txt" 2>/dev/null || true
    
    # Check for unencrypted HTTP
    grep -r --include="*.java" --include="*.js" --include="*.vue" --include="*.tsx" \
        -E "http://" \
        "$PROJECT_ROOT" > "$REPORTS_DIR/hipaa-encryption-check-$TIMESTAMP.txt" 2>/dev/null || true
    
    # PCI Compliance Checks
    log "Checking PCI compliance..."
    
    # Check for credit card patterns
    grep -r --include="*.java" --include="*.js" --include="*.vue" --include="*.tsx" \
        -E "\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b" \
        "$PROJECT_ROOT" > "$REPORTS_DIR/pci-cc-check-$TIMESTAMP.txt" 2>/dev/null || true
    
    # OWASP Top 10 Checks
    log "Checking OWASP Top 10 vulnerabilities..."
    
    # Check for SQL injection patterns
    grep -r --include="*.java" \
        -E "(executeQuery|executeUpdate|Statement)" \
        "$PROJECT_ROOT" > "$REPORTS_DIR/owasp-sql-injection-$TIMESTAMP.txt" 2>/dev/null || true
    
    # Check for XSS patterns
    grep -r --include="*.js" --include="*.vue" --include="*.tsx" \
        -E "(innerHTML|document\\.write|eval\\()" \
        "$PROJECT_ROOT" > "$REPORTS_DIR/owasp-xss-$TIMESTAMP.txt" 2>/dev/null || true
    
    # GDPR Compliance Checks
    log "Checking GDPR compliance..."
    
    # Check for consent management
    grep -r --include="*.java" --include="*.js" --include="*.vue" --include="*.tsx" \
        -E "(consent|permission|opt.*in)" \
        "$PROJECT_ROOT" > "$REPORTS_DIR/gdpr-consent-check-$TIMESTAMP.txt" 2>/dev/null || true
}

# Generate Security Report
generate_security_report() {
    log "📊 Generating comprehensive security report..."
    
    REPORT_FILE="$REPORTS_DIR/security-report-$TIMESTAMP.md"
    
    cat > "$REPORT_FILE" << EOF
# VoxCare Security Analysis Report

**Generated:** $TIMESTAMP  
**Project:** VoxCare Telehealth Platform  
**Scope:** HIPAA, PCI, HL7, GDPR, and OWASP Compliance

## Executive Summary

This report contains the results of comprehensive security analysis performed on the VoxCare codebase.

## Analysis Results

### 1. Java Backend Security Analysis

#### SpotBugs Security Findings
EOF

    # Add SpotBugs results
    for report in "$REPORTS_DIR"/spotbugs-*.txt; do
        if [ -f "$report" ]; then
            echo "**$(basename "$report" .txt):**" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            cat "$report" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

#### Dependency Vulnerabilities
EOF

    # Add dependency check results
    for report in "$REPORTS_DIR"/dependency-check-*; do
        if [ -d "$report" ]; then
            echo "**$(basename "$report"):**" >> "$REPORT_FILE"
            if [ -f "$report/dependency-check-report.html" ]; then
                echo "- HTML Report: $(basename "$report")/dependency-check-report.html" >> "$REPORT_FILE"
            fi
            if [ -f "$report/dependency-check-report.json" ]; then
                echo "- JSON Report: $(basename "$report")/dependency-check-report.json" >> "$REPORT_FILE"
            fi
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

### 2. Frontend Security Analysis

#### NPM Security Audit Results
EOF

    # Add npm audit results
    for report in "$REPORTS_DIR"/npm-audit-*.txt; do
        if [ -f "$report" ]; then
            echo "**$(basename "$report" .txt):**" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            cat "$report" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

### 3. Semgrep Security Analysis

#### Automated Security Rule Violations
EOF

    if [ -f "$REPORTS_DIR/semgrep-$TIMESTAMP.txt" ]; then
        echo '```' >> "$REPORT_FILE"
        cat "$REPORTS_DIR/semgrep-$TIMESTAMP.txt" >> "$REPORT_FILE"
        echo '```' >> "$REPORT_FILE"
    fi

    cat >> "$REPORT_FILE" << EOF

### 4. Custom Compliance Checks

#### HIPAA Compliance
EOF

    # Add HIPAA check results
    for report in "$REPORTS_DIR"/hipaa-*.txt; do
        if [ -f "$report" ]; then
            echo "**$(basename "$report" .txt):**" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            cat "$report" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

#### PCI Compliance
EOF

    # Add PCI check results
    for report in "$REPORTS_DIR"/pci-*.txt; do
        if [ -f "$report" ]; then
            echo "**$(basename "$report" .txt):**" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            cat "$report" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

#### OWASP Top 10
EOF

    # Add OWASP check results
    for report in "$REPORTS_DIR"/owasp-*.txt; do
        if [ -f "$report" ]; then
            echo "**$(basename "$report" .txt):**" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            cat "$report" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

#### GDPR Compliance
EOF

    # Add GDPR check results
    for report in "$REPORTS_DIR"/gdpr-*.txt; do
        if [ -f "$report" ]; then
            echo "**$(basename "$report" .txt):**" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            cat "$report" >> "$REPORT_FILE"
            echo '```' >> "$REPORT_FILE"
            echo "" >> "$REPORT_FILE"
        fi
    done

    cat >> "$REPORT_FILE" << EOF

## Recommendations

1. **Immediate Actions Required:**
   - Review all CRITICAL findings
   - Address high-severity vulnerabilities
   - Implement missing security controls

2. **Short-term Improvements:**
   - Update vulnerable dependencies
   - Implement security headers
   - Add input validation

3. **Long-term Strategy:**
   - Establish security training program
   - Implement automated security testing
   - Regular security assessments

## Compliance Status

- **HIPAA:** [To be determined based on findings]
- **PCI DSS:** [To be determined based on findings]
- **HL7 FHIR:** [To be determined based on findings]
- **GDPR:** [To be determined based on findings]
- **OWASP Top 10:** [To be determined based on findings]

## Next Steps

1. Review this report with the development team
2. Prioritize findings by severity and business impact
3. Create remediation plan with timelines
4. Schedule follow-up security review

---

*Report generated by VoxCare Security Linting Tool*
EOF

    log "Security report generated: $REPORT_FILE"
}

# Main execution
main() {
    log "Starting security analysis..."
    
    # Install required tools
    install_tools
    
    # Run all security analyses
    analyze_java_security
    analyze_frontend_security
    run_semgrep_analysis
    run_custom_security_analysis
    
    # Generate comprehensive report
    generate_security_report
    
    log "Security analysis completed successfully!"
    log "Reports available in: $REPORTS_DIR"
    log "Main report: security-report-$TIMESTAMP.md"
    
    echo ""
    echo -e "${GREEN}✅ Security analysis completed!${NC}"
    echo -e "${BLUE}📁 Reports directory: $REPORTS_DIR${NC}"
    echo -e "${BLUE}📊 Main report: security-report-$TIMESTAMP.md${NC}"
}

# Run main function
main "$@"
