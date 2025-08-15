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
