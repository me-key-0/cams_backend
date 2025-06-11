# Course Service

The Course Service manages courses, course sessions, enrollments, and lecturer assignments for the CAMS system.

## 🎯 Purpose

- Course catalog management
- Course session scheduling
- Student enrollment tracking
- Lecturer assignment management
- Academic year and semester organization

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- User Service running (for lecturer validation)

### Configuration

```yaml
server:
  port: 8764

spring:
  application:
    name: course-service
  datasource:
    url: jdbc:mariadb://localhost:3306/course_db
    username: root
    password: $904380
```

### Running the Service

```bash
cd course-service
mvn spring-boot:run
```

## 📡 API Endpoints

### Course Management

#### Create Course
**POST** `/api/v1/courses`

**Request Body:**
```json
{
  "code": "CS101",
  "name": "Introduction to Computer Science",
  "creditHour": 3,
  "description": "Basic concepts of computer science",
  "departmentId": "1",
  "prerequisites": []
}
```

**Response:**
```json
{
  "id": 1,
  "code": "CS101",
  "name": "Introduction to Computer Science",
  "creditHour": 3,
  "description": "Basic concepts of computer science",
  "departmentId": "1",
  "prerequisites": []
}
```

#### Get Course by ID
**GET** `/api/v1/courses/{id}`

**Response:**
```json
{
  "id": 1,
  "code": "CS101",
  "name": "Introduction to Computer Science",
  "creditHour": 3,
  "description": "Basic concepts of computer science",
  "departmentId": "1",
  "prerequisites": []
}
```

#### Get All Courses
**GET** `/api/v1/courses`

**Response:**
```json
[
  {
    "id": 1,
    "code": "CS101",
    "name": "Introduction to Computer Science",
    "creditHour": 3,
    "description": "Basic concepts of computer science",
    "departmentId": "1",
    "prerequisites": []
  }
]
```

#### Update Course
**PUT** `/api/v1/courses/{id}`

**Request Body:**
```json
{
  "code": "CS101",
  "name": "Introduction to Computer Science - Updated",
  "creditHour": 3,
  "description": "Updated description",
  "departmentId": "1",
  "prerequisites": []
}
```

#### Delete Course
**DELETE** `/api/v1/courses/{id}`

### Course Session Management

#### Get Course Session
**GET** `/api/session/{id}`

**Response:**
```json
{
  "id": 1,
  "year": 2,
  "semester": 1,
  "academicYear": 2024,
  "course": {
    "code": "CS101",
    "name": "Introduction to Computer Science",
    "creditHour": 3
  }
}
```

#### Check Course Session Exists
**GET** `/api/session/{id}/exists`

**Response:**
```json
true
```

### Enrollment Management

#### Get Student Course Sessions
**GET** `/api/enrollment/sessions/{studentId}/{year}/{semester}/{academicYear}`

**Response:**
```json
[
  {
    "id": 1,
    "year": 2,
    "semester": 1,
    "academicYear": 2024,
    "course": {
      "code": "CS101",
      "name": "Introduction to Computer Science",
      "creditHour": 3
    },
    "lecturerName": "Dr. Jane Smith"
  }
]
```

#### Get Course Sessions by Student ID
**GET** `/api/enrollment/student/{studentId}`

**Response:**
```json
[
  {
    "id": 1,
    "year": 2,
    "semester": 1,
    "academicYear": 2024,
    "course": {
      "code": "CS101",
      "name": "Introduction to Computer Science",
      "creditHour": 3
    },
    "lecturerName": "Dr. Jane Smith"
  }
]
```

### Assignment Management

#### Get Lecturer Course Sessions
**GET** `/api/assignment/sessions/{lecturerId}`

**Response:**
```json
[
  {
    "id": 1,
    "academicYear": 2024,
    "semester": 1,
    "year": 2,
    "course": {
      "id": 1,
      "code": "CS101",
      "name": "Introduction to Computer Science",
      "creditHour": 3
    },
    "status": "ACTIVE"
  }
]
```

#### Get Lecturer Course Sessions (Alternative)
**GET** `/api/assignment/lecturer/{lecturerId}`

**Response:**
```json
[
  {
    "id": 1,
    "academicYear": 2024,
    "semester": 1,
    "year": 2,
    "course": {
      "id": 1,
      "code": "CS101",
      "name": "Introduction to Computer Science",
      "creditHour": 3
    },
    "status": "ACTIVE"
  }
]
```

#### Validate Lecturer for Course Session
**GET** `/api/assignment/lecturer/{lecturerId}/validate/{courseSessionId}`

**Response:**
```json
true
```

## 🏗️ Data Models

### Course Entity

```java
@Entity
public class Course {
    private Long id;
    private String code;           // e.g., "CS101"
    private String name;           // e.g., "Introduction to Computer Science"
    private int creditHour;        // e.g., 3
    private String description;
    private String departmentId;
    private List<Course> prerequisites;
}
```

### Course Session Entity

```java
@Entity
public class CourseSession {
    private Long id;
    private Integer academicYear;  // e.g., 2024
    private Integer semester;      // e.g., 1 or 2
    private Integer year;          // e.g., 1, 2, 3, 4 (student year)
    private Course course;
    private Long departmentId;
    private List<Long> lecturerId; // Multiple lecturers possible
    private Status status;         // ACTIVE, UPCOMING, COMPLETED
}
```

### Enrollment Entity

```java
@Entity
public class Enrollment {
    private Long id;
    private Long studentId;
    private String enrollmentDate;
    private Boolean isActive;
    private CourseSession courseSession;
}
```

### Assignment Entity

```java
@Entity
public class Assignment {
    private Long id;
    private Long lecturerId;
    private Status status;         // ACTIVE, UPCOMING, COMPLETED
    private CourseSession courseSession;
}
```

## 🔧 Features

### Course Management

- Complete CRUD operations for courses
- Course prerequisite tracking
- Department-based course organization
- Credit hour management

### Session Management

- Academic year and semester organization
- Student year level tracking
- Multiple lecturer assignments
- Session status management

### Enrollment Tracking

- Student course enrollment
- Active enrollment status
- Historical enrollment data
- Enrollment date tracking

### Lecturer Assignments

- Multiple lecturers per course session
- Assignment status tracking
- Lecturer validation for course access
- Assignment history

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid course data
- **404 Not Found**: Course/session not found
- **409 Conflict**: Duplicate course code
- **500 Internal Server Error**: Database error

## 🧪 Testing

### Manual Testing Examples

```bash
# Create a course
curl -X POST http://localhost:8760/api/v1/courses \
  -H "Content-Type: application/json" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "code": "CS101",
    "name": "Introduction to Computer Science",
    "creditHour": 3,
    "description": "Basic concepts of computer science",
    "departmentId": "1"
  }'

# Get all courses
curl -X GET http://localhost:8760/api/v1/courses \
  -H "X-User-Role: STUDENT"

# Get student enrollments
curl -X GET http://localhost:8760/api/enrollment/student/1 \
  -H "X-User-Role: STUDENT"

# Get lecturer assignments
curl -X GET http://localhost:8760/api/assignment/lecturer/1 \
  -H "X-User-Role: LECTURER"

# Validate lecturer access
curl -X GET http://localhost:8760/api/assignment/lecturer/1/validate/1 \
  -H "X-User-Role: LECTURER"
```

## 📊 Database Schema

### Core Tables

- `course`: Course catalog
- `course_session`: Course offerings per semester
- `enrollment`: Student enrollments
- `assignment`: Lecturer assignments
- `prerequisites`: Course prerequisites (many-to-many)

### Relationships

- Course → CourseSession (One-to-Many)
- CourseSession → Enrollment (One-to-Many)
- CourseSession → Assignment (One-to-Many)
- Course → Prerequisites (Many-to-Many)

## 🔐 Security Features

- Role-based access control
- Department-level data isolation
- Lecturer validation for course access
- Secure enrollment management

## 📝 Integration Points

### User Service Integration

- Lecturer information retrieval
- User role validation
- Department association

### Used By

- **Grade Service**: Course session validation
- **Resource Service**: Course session validation
- **Communication Service**: Course session information

## 🚨 Troubleshooting

### Common Issues

1. **Course Session Not Found**
   - Verify course session ID
   - Check if session is active

2. **Lecturer Validation Fails**
   - Ensure lecturer is assigned to course session
   - Check assignment status

3. **Enrollment Issues**
   - Verify student ID
   - Check enrollment status

### Debug Logging

```yaml
logging:
  level:
    com.cams.course_service: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

## 📈 Performance Considerations

- Database indexing on frequently queried fields
- Efficient enrollment queries
- Caching for course catalog
- Optimized lecturer validation

## 🔄 Future Enhancements

- Course capacity management
- Waitlist functionality
- Automated enrollment
- Course recommendation system
- Advanced prerequisite checking