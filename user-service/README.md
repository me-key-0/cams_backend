# User Service

The User Service manages user accounts, profiles, and role-based access for the CAMS system.

## 🎯 Purpose

- User account management
- Profile information storage
- Role-based access control
- Lecturer evaluation system
- Department and college management

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+

### Configuration

```yaml
server:
  port: 8763

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:mariadb://localhost:3306/user_db
    username: root
    password: $904380
```

### Running the Service

```bash
cd user-service
mvn spring-boot:run
```

## 📡 API Endpoints

### User Management

#### Get All Users
**GET** `/api/users`

**Headers:**
- `X-User-Role`: ADMIN, LECTURER, STUDENT

**Response:**
```json
[
  {
    "id": 1,
    "firstname": "John",
    "lastname": "Doe",
    "email": "john.doe@example.com",
    "role": "STUDENT",
    "isVerified": true
  }
]
```

#### Create User
**POST** `/api/users`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

#### Delete User
**DELETE** `/api/users/{id}`

**Headers:**
- `X-User-Role`: ADMIN

#### Validate Credentials
**GET** `/api/users/validate`

**Query Parameters:**
- `email`: User email
- `password`: User password

**Response:**
```json
true
```

#### Get User by Email
**GET** `/api/users/email/{email}`

**Response:**
```json
{
  "id": 1,
  "departmentId": 1,
  "email": "john.doe@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "role": "STUDENT",
  "profileImage": null,
  "isVerified": true
}
```

### Lecturer Management

#### Get Lecturer by ID
**GET** `/api/users/lecturer/{lecturerId}`

**Response:**
```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "department": "Computer Science"
}
```

#### Get Lecturer by User ID
**GET** `/api/users/lecturer/user/{userId}`

**Response:**
```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "department": "Computer Science"
}
```

### Evaluation System

#### Submit Evaluation
**POST** `/api/v1/evaluation/submit/{studentId}`

**Request Body:**
```json
{
  "lecturerId": 1,
  "answers": [
    {
      "questionId": 1,
      "answerId": 3
    },
    {
      "questionId": 2,
      "answerId": 4
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Evaluation Submitted Successfully!"
}
```

#### Activate Evaluation Session
**POST** `/api/v1/evaluation/session/{sessionId}`

**Response:**
```json
{
  "success": true,
  "message": "Session Activated Successfully"
}
```

#### Check Session Status
**GET** `/api/v1/evaluation/session/{sessionId}`

**Response:**
```json
{
  "success": true,
  "message": "Evaluation Session is Active"
}
```

#### Get Evaluation Questions
**GET** `/api/v1/evaluation/questions`

**Response:**
```json
[
  {
    "id": 1,
    "question": "How would you rate the lecturer's teaching methodology?"
  },
  {
    "id": 2,
    "question": "How clear were the course objectives?"
  }
]
```

## 🏗️ Data Models

### User Roles

- `STUDENT`: Regular student access
- `LECTURER`: Faculty member access
- `ADMIN`: Department administrator
- `SUPER_ADMIN`: System administrator

### User Entity

```java
@Entity
public class User {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password; // BCrypt hashed
    private String role;
    private String phoneNumber;
    private String profileImage;
    private boolean isVerified;
    private Department department;
}
```

### Department Entity

```java
@Entity
public class Department {
    private Long id;
    private String name;
    private Integer years;
    private College college;
    private List<User> users;
}
```

### Evaluation System

The evaluation system allows students to evaluate lecturers:

- **EvaluationQuestion**: Predefined questions
- **EvaluationOption**: Answer options (1-5 scale)
- **Evaluation**: Student's evaluation of a lecturer
- **EvaluationSession**: Controls when evaluations are active

## 🔧 Features

### Password Security

- Passwords are hashed using BCrypt
- Minimum password requirements enforced
- Secure password validation

### Role-Based Access

- Different access levels for different user types
- Department-based access control
- Hierarchical permission system

### Evaluation System

- Anonymous lecturer evaluations
- Session-based evaluation periods
- Comprehensive question framework
- Statistical analysis support

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: User/resource not found
- **500 Internal Server Error**: System error

## 🧪 Testing

### Manual Testing Examples

```bash
# Get all users
curl -X GET http://localhost:8760/api/users \
  -H "X-User-Role: ADMIN"

# Validate credentials
curl -X GET "http://localhost:8760/api/users/validate?email=student@example.com&password=password123"

# Get lecturer details
curl -X GET http://localhost:8760/api/users/lecturer/1

# Submit evaluation
curl -X POST http://localhost:8760/api/v1/evaluation/submit/1 \
  -H "Content-Type: application/json" \
  -d '{
    "lecturerId": 1,
    "answers": [
      {"questionId": 1, "answerId": 4},
      {"questionId": 2, "answerId": 5}
    ]
  }'
```

## 📊 Database Schema

### Core Tables

- `users`: Main user information
- `students`: Student-specific data
- `lecturers`: Lecturer-specific data
- `admins`: Administrator data
- `departments`: Department information
- `colleges`: College information

### Evaluation Tables

- `evaluation_questions`: Question bank
- `evaluation_options`: Answer options
- `evaluations`: Student evaluations
- `evaluation_sessions`: Session management

## 🔐 Security Features

- BCrypt password hashing
- Role-based access control
- Department-level data isolation
- Secure credential validation
- Anonymous evaluation system

## 📝 Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8763 | Service port |
| `spring.jpa.hibernate.ddl-auto` | update | Database schema management |
| `spring.jpa.show-sql` | true | SQL query logging |

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection**
   - Verify MariaDB is running
   - Check database credentials
   - Ensure user_db exists

2. **Password Validation Fails**
   - Check BCrypt encoding
   - Verify password format

3. **Role Access Denied**
   - Verify user role assignment
   - Check department associations

### Debug Logging

```yaml
logging:
  level:
    com.cams.user_service: DEBUG
    org.springframework.security: DEBUG
```