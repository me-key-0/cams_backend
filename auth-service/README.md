# Auth Service

The Authentication Service handles user authentication and JWT token management for the CAMS system.

## 🎯 Purpose

- User authentication via email/password
- JWT token generation and validation
- Integration with User Service for credential verification

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- User Service running

### Configuration

```yaml
server:
  port: 8762

spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:mariadb://localhost:3306/auth_db
    username: root
    password: $904380

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: ${JWT_EXPIRATION:86400000}
```

### Running the Service

```bash
cd auth-service
mvn spring-boot:run
```

## 📡 API Endpoints

### Authentication

#### Login
Authenticate user and receive JWT token.

**POST** `/api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstname": "John",
    "lastname": "Doe",
    "role": "STUDENT",
    "profileImage": null
  }
}
```

**Status Codes:**
- `200 OK` - Login successful
- `400 Bad Request` - Invalid request format
- `401 Unauthorized` - Invalid credentials
- `403 Forbidden` - Account not verified

## 🔧 Features

### JWT Token Structure

The JWT token contains the following claims:
- `sub`: User ID
- `email`: User email
- `role`: User role (STUDENT, LECTURER, ADMIN, SUPER_ADMIN)
- `department`: Department ID
- `iat`: Issued at timestamp
- `exp`: Expiration timestamp

### Security

- Passwords are validated against hashed versions in User Service
- JWT tokens are signed with HMAC SHA-256
- Tokens expire after 24 hours (configurable)
- Account verification status is checked during login

### Integration

The Auth Service integrates with:
- **User Service**: For credential validation and user details
- **API Gateway**: Provides JWT validation for protected routes

## 🏗️ Architecture

### Components

- **AuthController**: REST endpoints for authentication
- **AuthService**: Business logic for authentication
- **JwtTokenProvider**: JWT token generation and validation
- **UserServiceClient**: Feign client for User Service integration

### Dependencies

- Spring Boot Security
- Spring Cloud OpenFeign
- JWT (jjwt library)
- Spring Cloud Netflix Eureka Client

## 🔍 Error Handling

The service provides comprehensive error handling:

- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Invalid credentials
- **403 Forbidden**: Account not verified
- **500 Internal Server Error**: Service unavailable

## 📊 Monitoring

### Health Check

**GET** `/actuator/health`

### Service Registration

The service registers with Eureka at startup and can be discovered by other services.

## 🧪 Testing

### Manual Testing

```bash
# Login request
curl -X POST http://localhost:8760/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "password123"
  }'
```

### Integration Testing

The service includes integration tests for:
- Successful authentication
- Invalid credentials handling
- Account verification checks
- JWT token generation

## 🔐 Security Considerations

- JWT secret should be strong and kept secure
- Token expiration should be appropriate for your use case
- Consider implementing refresh tokens for long-lived sessions
- Monitor for brute force attacks
- Implement rate limiting for login attempts

## 📝 Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `jwt.secret` | Generated | JWT signing secret |
| `jwt.expiration` | 86400000 | Token expiration (24h) |
| `server.port` | 8762 | Service port |

## 🚨 Troubleshooting

### Common Issues

1. **User Service Unavailable**
   - Ensure User Service is running
   - Check Eureka registration

2. **Invalid JWT Secret**
   - Verify JWT_SECRET environment variable
   - Ensure secret is consistent across services

3. **Database Connection Issues**
   - Check MariaDB is running
   - Verify database credentials
   - Ensure auth_db database exists

### Logs

Enable debug logging for troubleshooting:
```yaml
logging:
  level:
    com.cams.auth_service: DEBUG
```