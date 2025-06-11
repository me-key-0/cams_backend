# Grade Service

The Grade Service manages assessments, submissions, grading, and grade reports for the CAMS system.

## 🎯 Purpose

- Assessment creation and management
- Student submission tracking
- Grade calculation and reporting
- Academic performance analytics
- Grade type management

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- Course Service running (for course session validation)

### Configuration

```yaml
server:
  port: 8766

spring:
  application:
    name: grade-service
  datasource:
    url: jdbc:mariadb://localhost:3306/grade_db
    username: root
    password: $904380
```

### Running the Service

```bash
cd grade-service
mvn spring-boot:run
```

## 📡 API Endpoints

### Assessment Management

#### Create Assessment
**POST** `/api/assessments`

**Request Body:**
```json
{
  "title": "Midterm Exam",
  "description": "Midterm examination covering chapters 1-5",
  "type": "ASSIGNMENT",
  "isGroupWork": false,
  "courseSessionId": 1,
  "deadline": "2024-03-15T23:59:59"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Midterm Exam",
  "description": "Midterm examination covering chapters 1-5",
  "type": "ASSIGNMENT",
  "isGroupWork": false,
  "courseSessionId": 1,
  "deadline": "2024-03-15T23:59:59"
}
```

#### Get All Assessments
**GET** `/api/assessments`

**Response:**
```json
[
  {
    "id": 1,
    "title": "Midterm Exam",
    "description": "Midterm examination covering chapters 1-5",
    "type": "ASSIGNMENT",
    "isGroupWork": false,
    "courseSessionId": 1,
    "deadline": "2024-03-15T23:59:59"
  }
]
```

#### Get Assessment by ID
**GET** `/api/assessments/{id}`

**Response:**
```json
{
  "id": 1,
  "title": "Midterm Exam",
  "description": "Midterm examination covering chapters 1-5",
  "type": "ASSIGNMENT",
  "isGroupWork": false,
  "courseSessionId": 1,
  "deadline": "2024-03-15T23:59:59"
}
```

#### Get Assessments by Course
**GET** `/api/assessments/course/{courseId}`

**Response:**
```json
[
  {
    "id": 1,
    "title": "Midterm Exam",
    "description": "Midterm examination covering chapters 1-5",
    "type": "ASSIGNMENT",
    "isGroupWork": false,
    "courseSessionId": 1,
    "deadline": "2024-03-15T23:59:59"
  }
]
```

#### Update Assessment
**PUT** `/api/assessments/{id}`

**Request Body:**
```json
{
  "title": "Updated Midterm Exam",
  "description": "Updated description",
  "type": "ASSIGNMENT",
  "isGroupWork": false,
  "courseSessionId": 1,
  "deadline": "2024-03-16T23:59:59"
}
```

#### Delete Assessment
**DELETE** `/api/assessments/{id}`

### Submission Management

#### Submit Assessment
**POST** `/api/submissions`

**Request Body:**
```json
{
  "assessmentId": 1,
  "studentId": 123,
  "submissionUrl": "https://example.com/submission.pdf",
  "submittedAt": "2024-03-14T10:30:00",
  "score": null
}
```

**Response:**
```json
{
  "id": 1,
  "assessmentId": 1,
  "studentId": 123,
  "submissionUrl": "https://example.com/submission.pdf",
  "submittedAt": "2024-03-14T10:30:00",
  "score": null
}
```

#### Get Submissions by Assessment
**GET** `/api/submissions/assessment/{assessmentId}`

**Response:**
```json
[
  {
    "id": 1,
    "assessmentId": 1,
    "studentId": 123,
    "submissionUrl": "https://example.com/submission.pdf",
    "submittedAt": "2024-03-14T10:30:00",
    "score": 85.5
  }
]
```

#### Get Submission by ID
**GET** `/api/submissions/{id}`

**Response:**
```json
{
  "id": 1,
  "assessmentId": 1,
  "studentId": 123,
  "submissionUrl": "https://example.com/submission.pdf",
  "submittedAt": "2024-03-14T10:30:00",
  "score": 85.5
}
```

#### Update Submission
**PUT** `/api/submissions/{id}`

**Request Body:**
```json
{
  "assessmentId": 1,
  "studentId": 123,
  "submissionUrl": "https://example.com/updated-submission.pdf",
  "submittedAt": "2024-03-14T10:30:00",
  "score": 90.0
}
```

#### Delete Submission
**DELETE** `/api/submissions/{id}`

### Grade Report Management

#### Post Final Grade
**POST** `/api/grades/grade_reports`

**Request Body:**
```json
{
  "studentId": 123,
  "courseId": 1,
  "finalGrade": 87.5
}
```

**Response:**
```json
{
  "id": 1,
  "studentId": 123,
  "courseSessionId": 1,
  "finalGrade": 87.5,
  "assessmentGrade": []
}
```

#### Get Final Grades
**GET** `/api/grades/grade_reports/student/{studentId}/{year}/{semester}`

**Response:**
```json
[
  {
    "courseCode": "CS101",
    "courseName": "Introduction to Computer Science",
    "creditHour": 3,
    "finalGrade": 87.5
  },
  {
    "courseCode": "MATH201",
    "courseName": "Calculus II",
    "creditHour": 4,
    "finalGrade": 92.0
  }
]
```

### Grade Type Management

#### Create Grade Type
**POST** `/api/grade_types`

**Request Body:**
```json
{
  "name": "Quiz"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Quiz",
  "value": null,
  "courseSessionId": null
}
```

#### Get All Grade Types
**GET** `/api/grade_types`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Quiz",
    "value": null,
    "courseSessionId": null
  },
  {
    "id": 2,
    "name": "Midterm",
    "value": null,
    "courseSessionId": null
  }
]
```

### Assessment Grade Management

#### Delete Assessment Grade
**DELETE** `/api/assessment_grades/{id}`

**Response:**
```json
"Deleted Successfully"
```

## 🏗️ Data Models

### Assessment Entity

```java
@Entity
public class Assessment {
    private Long id;
    private Long courseSessionId;
    private AssessmentType type;        // ASSIGNMENT, PROJECT
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private SubmissionMode submissionMode; // INDIVIDUAL, GROUP
    private List<AssessmentSubmission> submissions;
}
```

### Assessment Submission Entity

```java
@Entity
public class AssessmentSubmission {
    private Long id;
    private Long studentId;
    private Assessment assessment;
    private String fileUrl;
    private String comment;
    private LocalDateTime submittedAt;
    private Double score;
}
```

### Grade Report Entity

```java
@Entity
public class GradeReport {
    private Long id;
    private Long studentId;
    private Long courseSessionId;
    private List<AssessmentGrade> assessmentGrade;
    private Double finalGrade;
}
```

### Grade Type Entity

```java
@Entity
public class GradeType {
    private Long id;
    private String name;
    private Integer value;
    private Long courseSessionId;
}
```

## 🔧 Features

### Assessment Management

- Create and manage assessments
- Support for individual and group work
- Deadline tracking
- Assessment type categorization

### Submission Tracking

- Student submission management
- File upload support
- Submission timestamp tracking
- Score assignment

### Grade Calculation

- Automated grade calculation
- Multiple assessment types
- Weighted grading support
- Final grade computation

### Reporting

- Student grade reports
- Course performance analytics
- Semester-wise grade tracking
- Academic year summaries

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid assessment data
- **404 Not Found**: Assessment/submission not found
- **409 Conflict**: Duplicate submission
- **500 Internal Server Error**: Database error

## 🧪 Testing

### Manual Testing Examples

```bash
# Create assessment
curl -X POST http://localhost:8760/api/assessments \
  -H "Content-Type: application/json" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "title": "Midterm Exam",
    "description": "Midterm examination",
    "type": "ASSIGNMENT",
    "isGroupWork": false,
    "courseSessionId": 1,
    "deadline": "2024-03-15T23:59:59"
  }'

# Submit assessment
curl -X POST http://localhost:8760/api/submissions \
  -H "Content-Type: application/json" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "assessmentId": 1,
    "studentId": 123,
    "submissionUrl": "https://example.com/submission.pdf",
    "submittedAt": "2024-03-14T10:30:00"
  }'

# Get student grades
curl -X GET http://localhost:8760/api/grades/grade_reports/student/123/2024/1 \
  -H "X-User-Role: STUDENT"

# Post final grade
curl -X POST http://localhost:8760/api/grades/grade_reports \
  -H "Content-Type: application/json" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "studentId": 123,
    "courseId": 1,
    "finalGrade": 87.5
  }'
```

## 📊 Database Schema

### Core Tables

- `assessment`: Assessment definitions
- `assessment_submission`: Student submissions
- `grade_report`: Final grades
- `grade_type`: Grade categories
- `assessment_grade`: Individual assessment grades

### Relationships

- Assessment → AssessmentSubmission (One-to-Many)
- GradeReport → AssessmentGrade (One-to-Many)
- GradeType → AssessmentGrade (One-to-Many)

## 🔐 Security Features

- Role-based access control
- Student can only submit their own work
- Lecturers can grade their course assessments
- Secure grade data handling

## 📝 Integration Points

### Course Service Integration

- Course session validation
- Student enrollment verification
- Course information retrieval

### Used By

- **Communication Service**: Grade notifications
- **User Service**: Academic performance tracking

## 🚨 Troubleshooting

### Common Issues

1. **Assessment Not Found**
   - Verify assessment ID
   - Check course session association

2. **Submission Fails**
   - Verify student enrollment
   - Check assessment deadline

3. **Grade Calculation Issues**
   - Verify assessment weights
   - Check grade type configuration

### Debug Logging

```yaml
logging:
  level:
    com.cams.grade_service: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

## 📈 Performance Considerations

- Database indexing on student and course IDs
- Efficient grade calculation algorithms
- Optimized reporting queries
- Caching for frequently accessed data

## 🔄 Future Enhancements

- Automated plagiarism detection
- Advanced analytics and insights
- Grade curve calculations
- Peer review systems
- Integration with external grading tools
- Mobile submission support