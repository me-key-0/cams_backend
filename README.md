# CAMS - College Academics Management System

A comprehensive microservices-based college management system built with Spring Boot and Spring Cloud.

## 🏗️ Architecture Overview

CAMS follows a microservices architecture pattern with the following services:

- **Service Discovery** - Eureka Server for service registration and discovery
- **API Gateway** - Central entry point with authentication and routing
- **Auth Service** - Authentication and authorization management
- **User Service** - User management and profiles
- **Course Service** - Course and enrollment management
- **Grade Service** - Assessment and grading system
- **Resource Service** - File and resource management
- **Communication Service** - Announcements, notifications, and tickets

## 🚀 Quick Start

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- Node.js 18+ (for frontend)

### Database Setup

Create the following databases in MariaDB:
```sql
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE course_db;
CREATE DATABASE grade_db;
CREATE DATABASE resource_db;
CREATE DATABASE communication_db;
```

### Environment Variables

Set the following environment variables:
```bash
export JWT_SECRET=your-jwt-secret-key
export JWT_EXPIRATION=86400000
```

### Running the Services

1. **Start Service Discovery** (Port: 8761)
```bash
cd service-discovery
mvn spring-boot:run
```

2. **Start API Gateway** (Port: 8760)
```bash
cd api-gateway
mvn spring-boot:run
```

3. **Start Individual Services**
```bash
# Auth Service (Port: 8762)
cd auth-service && mvn spring-boot:run

# User Service (Port: 8763)
cd user-service && mvn spring-boot:run

# Course Service (Port: 8764)
cd course-service && mvn spring-boot:run

# Communication Service (Port: 8765)
cd communication-service && mvn spring-boot:run

# Grade Service (Port: 8766)
cd grade-service && mvn spring-boot:run

# Resource Service (Port: 8767)
cd resource-service && mvn spring-boot:run
```

## 📊 Service Ports

| Service | Port | Purpose |
|---------|------|---------|
| Service Discovery | 8761 | Eureka Server |
| API Gateway | 8760 | Main entry point |
| Auth Service | 8762 | Authentication |
| User Service | 8763 | User management |
| Course Service | 8764 | Course management |
| Communication Service | 8765 | Communication features |
| Grade Service | 8766 | Grading system |
| Resource Service | 8767 | File management |

## 🔐 Authentication

All API requests (except auth endpoints) require JWT authentication:

```bash
Authorization: Bearer <jwt-token>
```

The API Gateway automatically validates tokens and forwards user information via headers:
- `X-User-Id`: User ID
- `X-User-Role`: User role (STUDENT, LECTURER, ADMIN, SUPER_ADMIN)
- `X-User-Department`: Department code

## 📚 API Documentation

Each service has its own README with detailed API documentation:

- [Auth Service](./auth-service/README.md)
- [User Service](./user-service/README.md)
- [Course Service](./course-service/README.md)
- [Grade Service](./grade-service/README.md)
- [Resource Service](./resource-service/README.md)
- [Communication Service](./communication-service/README.md)

## 🛠️ Development

### Building All Services

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

### Code Style

The project follows standard Java conventions with:
- Lombok for reducing boilerplate
- Spring Boot best practices
- RESTful API design
- Comprehensive error handling

## 🔧 Configuration

### Database Configuration

Each service uses MariaDB with the following pattern:
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/{service}_db
    username: root
    password: $904380
    driver-class-name: org.mariadb.jdbc.Driver
```

### Service Discovery

All services register with Eureka:
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

## 📁 Project Structure

```
cams-backend/
├── api-gateway/          # API Gateway service
├── auth-service/         # Authentication service
├── communication-service/ # Communication features
├── course-service/       # Course management
├── grade-service/        # Grading system
├── resource-service/     # File management
├── service-discovery/    # Eureka server
├── user-service/         # User management
└── pom.xml              # Parent POM
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.