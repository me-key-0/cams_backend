# Course Service

The Course Service manages courses, batches, course sessions, enrollments, and lecturer assignments for the CAMS system.

## 🎯 Purpose

- Course catalog management
- Batch and student cohort organization
- Course session scheduling and activation
- Student enrollment management
- Lecturer assignment and capacity tracking
- Academic year and semester progression

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- User Service running (for user validation)

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

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

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

### Batch Management

#### Create Batch
**POST** `/api/batches`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "name": "CS-2024-Batch-1",
  "admissionYear": 2024,
  "currentYear": 1,
  "currentSemester": 1,
  "departmentId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "name": "CS-2024-Batch-1",
  "admissionYear": 2024,
  "currentYear": 1,
  "currentSemester": 1,
  "departmentId": 1,
  "createdAt": "2024-01-15T10:30:00",
  "isActive": true,
  "courseAssignments": [],
  "totalStudents": 0
}
```

#### Get Batches by Department
**GET** `/api/batches/department/{departmentId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "name": "CS-2024-Batch-1",
    "admissionYear": 2024,
    "currentYear": 1,
    "currentSemester": 1,
    "departmentId": 1,
    "createdAt": "2024-01-15T10:30:00",
    "isActive": true,
    "courseAssignments": [
      {
        "id": 1,
        "batchId": 1,
        "batchName": "CS-2024-Batch-1",
        "courseId": 1,
        "courseCode": "CS101",
        "courseName": "Introduction to Computer Science",
        "creditHour": 3,
        "year": 1,
        "semester": 1,
        "assignedBy": 123,
        "assignedAt": "2024-01-15T10:35:00",
        "isActive": true
      }
    ],
    "totalStudents": 25
  }
]
```

#### Assign Courses to Batch
**POST** `/api/batches/course-assignments`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "batchId": 1,
  "courses": [
    {
      "courseId": 1,
      "year": 1,
      "semester": 1
    },
    {
      "courseId": 2,
      "year": 1,
      "semester": 1
    }
  ]
}
```

**Response:**
```json
[
  {
    "id": 1,
    "batchId": 1,
    "batchName": "CS-2024-Batch-1",
    "courseId": 1,
    "courseCode": "CS101",
    "courseName": "Introduction to Computer Science",
    "creditHour": 3,
    "year": 1,
    "semester": 1,
    "assignedBy": 123,
    "assignedAt": "2024-01-15T10:35:00",
    "isActive": true
  },
  {
    "id": 2,
    "batchId": 1,
    "batchName": "CS-2024-Batch-1",
    "courseId": 2,
    "courseCode": "CS102",
    "courseName": "Programming Fundamentals",
    "creditHour": 4,
    "year": 1,
    "semester": 1,
    "assignedBy": 123,
    "assignedAt": "2024-01-15T10:35:00",
    "isActive": true
  }
]
```

#### Advance Batch Semester
**POST** `/api/batches/{batchId}/advance-semester`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
{
  "id": 1,
  "name": "CS-2024-Batch-1",
  "admissionYear": 2024,
  "currentYear": 1,
  "currentSemester": 2,
  "departmentId": 1,
  "createdAt": "2024-01-15T10:30:00",
  "isActive": true,
  "courseAssignments": [],
  "totalStudents": 25
}
```

### Course Session Management

#### Create Course Session
**POST** `/api/course-sessions`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "academicYear": 2024,
  "semester": 1,
  "year": 1,
  "courseId": 1,
  "departmentId": 1,
  "lecturerIds": [123, 456]
}
```

**Response:**
```json
{
  "id": 1,
  "academicYear": 2024,
  "semester": 1,
  "year": 1,
  "course": {
    "name": "Introduction to Computer Science",
    "code": "CS101",
    "creditHour": 3
  },
  "departmentId": 1,
  "lecturers": [
    {
      "id": 123,
      "name": "Dr. Jane Smith",
      "email": "jane.smith@example.com"
    },
    {
      "id": 456,
      "name": "Dr. John Doe",
      "email": "john.doe@example.com"
    }
  ],
  "status": "UPCOMING",
  "isActive": false,
  "enrollmentOpen": false,
  "createdAt": "2024-01-15T10:40:00",
  "activatedAt": null,
  "createdBy": 789,
  "enrolledStudents": 0
}
```

#### Activate Course Session
**POST** `/api/course-sessions/{id}/activate`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
{
  "id": 1,
  "academicYear": 2024,
  "semester": 1,
  "year": 1,
  "course": {
    "name": "Introduction to Computer Science",
    "code": "CS101",
    "creditHour": 3
  },
  "departmentId": 1,
  "lecturers": [
    {
      "id": 123,
      "name": "Dr. Jane Smith",
      "email": "jane.smith@example.com"
    },
    {
      "id": 456,
      "name": "Dr. John Doe",
      "email": "john.doe@example.com"
    }
  ],
  "status": "ACTIVE",
  "isActive": true,
  "enrollmentOpen": false,
  "createdAt": "2024-01-15T10:40:00",
  "activatedAt": "2024-01-15T10:45:00",
  "createdBy": 789,
  "enrolledStudents": 0
}
```

#### Open Enrollment
**POST** `/api/course-sessions/{id}/open-enrollment`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
{
  "id": 1,
  "academicYear": 2024,
  "semester": 1,
  "year": 1,
  "course": {
    "name": "Introduction to Computer Science",
    "code": "CS101",
    "creditHour": 3
  },
  "departmentId": 1,
  "lecturers": [
    {
      "id": 123,
      "name": "Dr. Jane Smith",
      "email": "jane.smith@example.com"
    },
    {
      "id": 456,
      "name": "Dr. John Doe",
      "email": "john.doe@example.com"
    }
  ],
  "status": "ACTIVE",
  "isActive": true,
  "enrollmentOpen": true,
  "createdAt": "2024-01-15T10:40:00",
  "activatedAt": "2024-01-15T10:45:00",
  "createdBy": 789,
  "enrolledStudents": 0
}
```

### Lecturer Management

#### Set Lecturer Capacity
**POST** `/api/lecturer-management/capacity`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "lecturerId": 123,
  "departmentId": 1,
  "maxCreditHours": 12
}
```

**Response:**
```json
{
  "id": 1,
  "lecturerId": 123,
  "lecturerName": "Dr. Jane Smith",
  "departmentId": 1,
  "maxCreditHours": 12,
  "currentCreditHours": 0,
  "availableCreditHours": 12,
  "isActive": true
}
```

#### Assign Teachable Courses
**POST** `/api/lecturer-management/teachable-courses`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "lecturerId": 123,
  "courseIds": [1, 2, 3]
}
```

**Response:**
```json
[
  {
    "name": "Introduction to Computer Science",
    "code": "CS101",
    "creditHour": 3
  },
  {
    "name": "Programming Fundamentals",
    "code": "CS102",
    "creditHour": 4
  },
  {
    "name": "Data Structures",
    "code": "CS201",
    "creditHour": 3
  }
]
```

### Enrollment Management

#### Enroll Student
**POST** `/api/enrollment/enroll`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Query Parameters:**
- `studentId`: Student ID
- `courseSessionId`: Course session ID

**Response:**
```json
{
  "id": 1,
  "studentId": 456,
  "enrollmentDate": "2024-01-15T11:00:00",
  "isActive": true,
  "courseSession": {
    "id": 1,
    "academicYear": 2024,
    "semester": 1,
    "year": 1,
    "course": {
      "id": 1,
      "code": "CS101",
      "name": "Introduction to Computer Science",
      "creditHour": 3
    }
  }
}
```

#### Check Enrollment
**GET** `/api/enrollment/check-enrollment`

**Query Parameters:**
- `studentId`: Student ID
- `courseSessionId`: Course session ID

**Response:**
```json
true
```

#### Get Enrolled Students
**GET** `/api/enrollment/course-session/{courseSessionId}/students`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN, LECTURER

**Response:**
```json
[456, 789, 101]
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

### Batch Entity

```java
@Entity
public class Batch {
    private Long id;
    private String name;           // e.g., "CS-2024-Batch-1"
    private Integer admissionYear; // e.g., 2024
    private Integer currentYear;   // e.g., 1, 2, 3, 4
    private Integer currentSemester; // e.g., 1 or 2
    private Long departmentId;
    private Boolean isActive;
    private List<BatchCourseAssignment> courseAssignments;
}
```

### BatchCourseAssignment Entity

```java
@Entity
public class BatchCourseAssignment {
    private Long id;
    private Batch batch;
    private Course course;
    private Integer year;          // Which year this course is assigned to
    private Integer semester;      // Which semester (1 or 2)
    private Long assignedBy;       // Admin ID
    private Boolean isActive;
}
```

### CourseSession Entity

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
    private Status status;         // UPCOMING, ACTIVE, COMPLETED
    private Boolean isActive;      // Admin can activate/deactivate
    private Boolean enrollmentOpen; // Controls if students can enroll
    private Long createdBy;        // Admin ID
}
```

### LecturerCourseCapacity Entity

```java
@Entity
public class LecturerCourseCapacity {
    private Long id;
    private Long lecturerId;
    private Long departmentId;
    private Integer maxCreditHours;    // Maximum credit hours a lecturer can teach
    private Integer currentCreditHours; // Current assigned credit hours
    private Boolean isActive;
}
```

### LecturerTeachableCourse Entity

```java
@Entity
public class LecturerTeachableCourse {
    private Long id;
    private Long lecturerId;
    private Course course;
    private Boolean isActive;
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

## 🔧 Features

### Course Management

- Complete CRUD operations for courses
- Course prerequisite tracking
- Department-based course organization
- Credit hour management

### Batch Management

- Student cohort organization
- Semester progression tracking
- Course assignment to specific years/semesters
- Credit hour limit enforcement per semester

### Course Session Management

- Academic year and semester organization
- Multiple lecturer assignments
- Session activation/deactivation
- Enrollment control

### Lecturer Management

- Teaching capacity tracking
- Course teaching authorization
- Credit hour limit enforcement
- Department-based lecturer organization

### Enrollment Management

- Student course enrollment
- Enrollment status tracking
- Course session access control
- Enrollment verification

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid input data
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict (e.g., duplicate course code)
- **422 Unprocessable Entity**: Business rule violation (e.g., credit hour limit exceeded)
- **500 Internal Server Error**: System error

## 🧪 Testing

### Manual Testing Examples

```bash
# Create a batch
curl -X POST http://localhost:8760/api/batches \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "name": "CS-2024-Batch-1",
    "admissionYear": 2024,
    "currentYear": 1,
    "currentSemester": 1,
    "departmentId": 1
  }'

# Assign courses to batch
curl -X POST http://localhost:8760/api/batches/course-assignments \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "batchId": 1,
    "courses": [
      {
        "courseId": 1,
        "year": 1,
        "semester": 1
      },
      {
        "courseId": 2,
        "year": 1,
        "semester": 1
      }
    ]
  }'

# Set lecturer capacity
curl -X POST http://localhost:8760/api/lecturer-management/capacity \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "lecturerId": 456,
    "departmentId": 1,
    "maxCreditHours": 12
  }'

# Assign teachable courses to lecturer
curl -X POST http://localhost:8760/api/lecturer-management/teachable-courses \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "lecturerId": 456,
    "courseIds": [1, 2, 3]
  }'

# Create course session
curl -X POST http://localhost:8760/api/course-sessions \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "academicYear": 2024,
    "semester": 1,
    "year": 1,
    "courseId": 1,
    "departmentId": 1,
    "lecturerIds": [456]
  }'

# Activate course session
curl -X POST http://localhost:8760/api/course-sessions/1/activate \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN"

# Open enrollment
curl -X POST http://localhost:8760/api/course-sessions/1/open-enrollment \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN"

# Enroll student
curl -X POST "http://localhost:8760/api/enrollment/enroll?studentId=789&courseSessionId=1" \
  -H "X-User-Id: 789" \
  -H "X-User-Role: STUDENT"

# Get student course sessions
curl -X GET http://localhost:8760/api/enrollment/student/789 \
  -H "X-User-Role: STUDENT"
```

## 📊 Database Schema

### Core Tables

- `course`: Course catalog
- `batch`: Student cohorts
- `batch_course_assignment`: Courses assigned to batches
- `course_session`: Course offerings per semester
- `lecturer_course_capacity`: Lecturer teaching capacity
- `lecturer_teachable_course`: Courses a lecturer can teach
- `enrollment`: Student enrollments
- `course_session_lecturers`: Lecturers assigned to course sessions

### Relationships

- Batch → BatchCourseAssignment (One-to-Many)
- Course → BatchCourseAssignment (One-to-Many)
- Course → CourseSession (One-to-Many)
- CourseSession → Enrollment (One-to-Many)
- Course → LecturerTeachableCourse (One-to-Many)
- Course → Prerequisites (Many-to-Many)

## 🔐 Security Features

- Role-based access control
- Department-level data isolation
- Admin-only batch and course session management
- Lecturer assignment validation
- Credit hour limit enforcement
- Enrollment status verification

## 📝 Integration Points

### User Service Integration

- Lecturer information retrieval
- Student information retrieval
- Department association validation
- Admin authorization verification

### Used By

- **Grade Service**: Course session validation
- **Resource Service**: Course session validation
- **Communication Service**: Course session information

## 🚨 Troubleshooting

### Common Issues

1. **Credit Hour Limit Exceeded**
   - Check maximum credit hours per semester (24 by default)
   - Verify course credit hour values
   - Consider adjusting course assignments

2. **Lecturer Capacity Exceeded**
   - Check lecturer's maximum credit hours
   - Verify current assigned credit hours
   - Consider reassigning courses or increasing capacity

3. **Enrollment Issues**
   - Verify course session is active
   - Check enrollment is open
   - Ensure student is not already enrolled

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
- Batch processing for course assignments

## 🔄 Future Enhancements

- Course prerequisite validation during enrollment
- Automated batch progression
- Waitlist functionality for popular courses
- Course recommendation system
- Advanced scheduling with time slots and classrooms
- Conflict detection for lecturer assignments
- Student performance tracking integration
- Graduation requirement validation