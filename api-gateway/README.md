# API Gateway

The API Gateway serves as the central entry point for all client requests in the CAMS system, providing authentication, routing, and cross-cutting concerns.

## 🎯 Purpose

- Single entry point for all API requests
- JWT token validation and authentication
- Request routing to appropriate microservices
- Cross-origin resource sharing (CORS) handling
- Load balancing and service discovery integration

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- Service Discovery (Eureka) running
- All backend services running

### Configuration

```yaml
server:
  port: 8760

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**, /api/user/**
        # ... other routes

  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: ${JWT_EXPIRATION:86400000}
```

### Running the Service

```bash
cd api-gateway
mvn spring-boot:run
```

## 🛣️ Route Configuration

The API Gateway routes requests to the following services:

### Auth Service Routes
- **Path:** `/api/auth/**`
- **Service:** `auth-service`
- **Port:** 8762
- **Public:** Yes (no authentication required)

### User Service Routes
- **Path:** `/api/users/**`, `/api/user/**`
- **Service:** `user-service`
- **Port:** 8763
- **Public:** Partial (some endpoints public)

### Course Service Routes
- **Path:** `/api/courses/**`, `/api/enrollment/**`, `/api/assignment/**`, `/api/session/**`
- **Service:** `course-service`
- **Port:** 8764
- **Public:** No (authentication required)

### Communication Service Routes
- **Path:** `/api/com/**`
- **Service:** `communication-service`
- **Port:** 8765
- **Public:** No (authentication required)

### Grade Service Routes
- **Path:** `/api/grades/**`
- **Service:** `grade-service`
- **Port:** 8766
- **Public:** No (authentication required)

### Resource Service Routes
- **Path:** `/api/v1/resources/**`
- **Service:** `resource-service`
- **Port:** 8767
- **Public:** No (authentication required)

## 🔐 Authentication

### JWT Token Validation

The API Gateway validates JWT tokens for all protected routes:

```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    // Token validation logic
}
```

### Public Endpoints

The following endpoints do not require authentication:
- `/api/auth/login`
- `/api/auth/register`

### Protected Endpoints

All other endpoints require a valid JWT token in the Authorization header:

```bash
Authorization: Bearer <jwt-token>
```

### User Context Headers

After successful authentication, the gateway adds user context headers:

- `X-User-Id`: User ID from JWT token
- `X-User-Role`: User role (STUDENT, LECTURER, ADMIN, SUPER_ADMIN)
- `X-User-Department`: Department code from JWT token

## 🌐 CORS Configuration

The gateway handles CORS for frontend applications:

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);
        return new CorsWebFilter(source);
    }
}
```

## 📡 API Endpoints

### Health Check

#### Gateway Health
**GET** `/actuator/health`

**Response:**
```json
{
  "status": "UP",
  "components": {
    "gateway": {
      "status": "UP"
    }
  }
}
```

### Gateway Information

#### Get Gateway Routes
**GET** `/actuator/gateway/routes`

**Response:**
```json
[
  {
    "route_id": "auth-service",
    "route_definition": {
      "id": "auth-service",
      "uri": "lb://auth-service",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/api/auth/**"
          }
        }
      ]
    }
  }
]
```

## 🔧 Features

### Load Balancing

The gateway uses Spring Cloud LoadBalancer for service discovery and load balancing:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service  # Load balanced URI
```

### Circuit Breaker

Integration with resilience patterns:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          filters:
            - name: CircuitBreaker
              args:
                name: userServiceCircuitBreaker
```

### Rate Limiting

Request rate limiting per user:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: api-route
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

### Request/Response Logging

Comprehensive logging for debugging:

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    org.springframework.http.server.reactive: DEBUG
    org.springframework.web.reactive: DEBUG
```

## 🧪 Testing

### Authentication Testing

```bash
# Login to get token
TOKEN=$(curl -s -X POST http://localhost:8760/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"password123"}' \
  | jq -r '.token')

# Use token for authenticated request
curl -X GET http://localhost:8760/api/users \
  -H "Authorization: Bearer $TOKEN"
```

### Route Testing

```bash
# Test auth service route
curl -X POST http://localhost:8760/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Test user service route
curl -X GET http://localhost:8760/api/users \
  -H "Authorization: Bearer $TOKEN"

# Test course service route
curl -X GET http://localhost:8760/api/courses \
  -H "Authorization: Bearer $TOKEN"
```

### CORS Testing

```bash
# Test CORS preflight
curl -X OPTIONS http://localhost:8760/api/users \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization"
```

## 🔍 Error Handling

### Authentication Errors

- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Valid token but insufficient permissions

### Service Errors

- **503 Service Unavailable**: Backend service is down
- **504 Gateway Timeout**: Backend service timeout

### Example Error Response

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid",
  "path": "/api/users"
}
```

## 📊 Monitoring

### Actuator Endpoints

Available monitoring endpoints:

- `/actuator/health` - Health status
- `/actuator/info` - Application information
- `/actuator/gateway/routes` - Gateway routes
- `/actuator/metrics` - Application metrics

### Custom Metrics

```java
@Component
public class GatewayMetrics {
    private final Counter requestCounter;
    private final Timer requestTimer;
    
    // Metrics collection logic
}
```

## 🚨 Troubleshooting

### Common Issues

1. **Service Not Found (404)**
   - Check if target service is registered in Eureka
   - Verify route configuration
   - Ensure service is running

2. **Authentication Failures**
   - Verify JWT secret configuration
   - Check token expiration
   - Validate token format

3. **CORS Issues**
   - Check allowed origins configuration
   - Verify preflight request handling
   - Ensure credentials are allowed

### Debug Configuration

```yaml
logging:
  level:
    com.cams.api_gateway: DEBUG
    org.springframework.cloud.gateway: DEBUG
    org.springframework.security: DEBUG
```

## 🔐 Security Features

### JWT Token Validation

- HMAC SHA-256 signature verification
- Token expiration checking
- Claims extraction and validation

### Request Filtering

- Path-based access control
- Role-based authorization
- Request sanitization

### Security Headers

```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .headers(headers -> headers
            .frameOptions().deny()
            .contentTypeOptions()
            .and()
            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                .maxAgeInSeconds(31536000)
                .includeSubdomains(true)
            )
        )
        .build();
}
```

## 📈 Performance Considerations

### Connection Pooling

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 1000
          max-idle-time: 30s
```

### Caching

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: cached-route
          filters:
            - name: LocalResponseCache
              args:
                size: 10MB
                timeToLive: 5m
```

## 🔄 Future Enhancements

- API versioning support
- Advanced rate limiting strategies
- Request/response transformation
- API documentation integration
- Enhanced monitoring and alerting
- Service mesh integration
- Advanced security features
- Multi-tenant support

## 📝 Configuration Reference

### Gateway Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8760 | Gateway port |
| `spring.security.jwt.secret` | - | JWT signing secret |
| `spring.security.jwt.expiration` | 86400000 | Token expiration (24h) |

### Route Configuration

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: service-name
          uri: lb://service-name
          predicates:
            - Path=/api/path/**
          filters:
            - StripPrefix=1
```

This API Gateway provides a robust, secure, and scalable entry point for the CAMS system, ensuring proper authentication, routing, and cross-cutting concerns are handled efficiently.