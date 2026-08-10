# Contributing to VoxCare

Thank you for your interest in contributing to VoxCare Telehealth Platform!

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your-username/voxcare-telehealth.git`
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
cd voxcare-telehealth
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
