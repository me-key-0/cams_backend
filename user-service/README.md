# User Service

The User Service manages user accounts, profiles, role-based access, and the lecturer evaluation system for the CAMS platform.

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

#### Create Evaluation Session
**POST** `/api/v1/evaluation/session`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "courseSessionId": 1,
  "startDate": "2024-05-01T00:00:00",
  "endDate": "2024-05-15T23:59:59",
  "departmentId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "courseSessionId": 1,
  "isActive": false,
  "startDate": "2024-05-01T00:00:00",
  "endDate": "2024-05-15T23:59:59",
  "departmentId": 1,
  "activatedBy": 123,
  "courseCode": "CS101",
  "courseName": "Programming Fundamentals"
}
```

#### Activate Evaluation Session
**POST** `/api/v1/evaluation/session/{sessionId}/activate`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
{
  "success": true,
  "message": "Session Activated Successfully"
}
```

#### Get Session Status
**GET** `/api/v1/evaluation/session/{sessionId}/status`

**Response:**
```json
{
  "success": true,
  "message": "Evaluation Session is Active"
}
```

#### Get Evaluation Sessions by Department
**GET** `/api/v1/evaluation/sessions/department/{departmentId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "courseSessionId": 1,
    "isActive": true,
    "startDate": "2024-05-01T00:00:00",
    "endDate": "2024-05-15T23:59:59",
    "departmentId": 1,
    "activatedBy": 123,
    "courseCode": "CS101",
    "courseName": "Programming Fundamentals"
  }
]
```

#### Submit Evaluation
**POST** `/api/v1/evaluation/submit`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Request Body:**
```json
{
  "lecturerId": 1,
  "courseSessionId": 1,
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

#### Get Evaluation Questions
**GET** `/api/v1/evaluation/questions`

**Response:**
```json
[
  {
    "id": 1,
    "question": "How would you rate the lecturer's teaching methodology?",
    "category": {
      "id": 1,
      "name": "Teaching Methodology"
    }
  },
  {
    "id": 2,
    "question": "How clear were the course objectives?",
    "category": {
      "id": 2,
      "name": "Course Content"
    }
  }
]
```

#### Get Questions by Category
**GET** `/api/v1/evaluation/questions/category/{categoryId}`

**Response:**
```json
[
  {
    "id": 1,
    "question": "How would you rate the lecturer's teaching methodology?",
    "categoryId": 1,
    "categoryName": "Teaching Methodology"
  }
]
```

#### Get Evaluation Categories
**GET** `/api/v1/evaluation/categories`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Teaching Methodology",
    "description": "Evaluation of teaching methods and delivery"
  },
  {
    "id": 2,
    "name": "Course Content",
    "description": "Evaluation of course materials and content"
  }
]
```

### Evaluation Analytics

#### Get Course Evaluation Analytics
**GET** `/api/v1/evaluation/analytics/course/{courseSessionId}/lecturer/{lecturerId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
{
  "lecturerId": 1,
  "lecturerName": "Jane Smith",
  "courseSessionId": 1,
  "courseCode": "CS101",
  "courseName": "Programming Fundamentals",
  "totalSubmissions": 25,
  "overallRating": 4.2,
  "categoryRatings": {
    "Teaching Methodology": 4.5,
    "Course Content": 4.0,
    "Communication": 4.1
  },
  "questionAnalytics": [
    {
      "questionId": 1,
      "question": "How would you rate the lecturer's teaching methodology?",
      "category": "Teaching Methodology",
      "averageRating": 4.5,
      "ratingDistribution": {
        "1": 0,
        "2": 1,
        "3": 2,
        "4": 8,
        "5": 14
      }
    }
  ]
}
```

#### Get Lecturer Evaluation Analytics
**GET** `/api/v1/evaluation/analytics/lecturer/{lecturerId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "lecturerId": 1,
    "lecturerName": "Jane Smith",
    "courseSessionId": 1,
    "courseCode": "CS101",
    "courseName": "Programming Fundamentals",
    "totalSubmissions": 25,
    "overallRating": 4.2,
    "categoryRatings": {
      "Teaching Methodology": 4.5,
      "Course Content": 4.0
    },
    "questionAnalytics": [...]
  },
  {
    "lecturerId": 1,
    "lecturerName": "Jane Smith",
    "courseSessionId": 2,
    "courseCode": "CS102",
    "courseName": "Data Structures",
    "totalSubmissions": 18,
    "overallRating": 4.4,
    "categoryRatings": {
      "Teaching Methodology": 4.6,
      "Course Content": 4.2
    },
    "questionAnalytics": [...]
  }
]
```

#### Get Department Evaluation Analytics
**GET** `/api/v1/evaluation/analytics/department/{departmentId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "lecturerId": 1,
    "lecturerName": "Jane Smith",
    "courseSessionId": 1,
    "courseCode": "CS101",
    "courseName": "Programming Fundamentals",
    "totalSubmissions": 25,
    "overallRating": 4.2,
    "categoryRatings": {...},
    "questionAnalytics": [...]
  },
  {
    "lecturerId": 2,
    "lecturerName": "John Doe",
    "courseSessionId": 3,
    "courseCode": "CS201",
    "courseName": "Algorithms",
    "totalSubmissions": 22,
    "overallRating": 4.1,
    "categoryRatings": {...},
    "questionAnalytics": [...]
  }
]
```

## 🏗️ Data Models

### User Roles

- `STUDENT`: Regular student access
- `LECTURER`: Faculty member access
- `ADMIN`: Department administrator
- `SUPER_ADMIN`: System administrator

### Evaluation System

The evaluation system allows students to evaluate lecturers:

- **EvaluationCategory**: Groups questions by topic (Teaching, Content, etc.)
- **EvaluationQuestion**: Predefined questions organized by category
- **EvaluationOption**: Answer options (1-5 scale)
- **Evaluation**: Student's evaluation of a lecturer
- **EvaluationSession**: Controls when evaluations are active for a course
- **EvaluationAnswer**: Individual answers to evaluation questions

## 🔧 Features

### Evaluation System

- **Categorized Questions**: Questions organized by topic areas
- **Session-based Evaluations**: Controlled evaluation periods
- **Anonymous Submissions**: Student identities protected in analytics
- **Comprehensive Analytics**: Detailed statistical analysis
- **Visual Reporting**: Charts and graphs for easy interpretation
- **Multi-level Access**: Department and system-wide analytics
- **Course-specific Evaluations**: Targeted feedback for each course

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: System error

## 🧪 Testing

### Manual Testing Examples

```bash
# Create evaluation session
curl -X POST http://localhost:8760/api/v1/evaluation/session \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "courseSessionId": 1,
    "startDate": "2024-05-01T00:00:00",
    "endDate": "2024-05-15T23:59:59",
    "departmentId": 1
  }'

# Activate evaluation session
curl -X POST http://localhost:8760/api/v1/evaluation/session/1/activate \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN"

# Submit evaluation
curl -X POST http://localhost:8760/api/v1/evaluation/submit \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "lecturerId": 1,
    "courseSessionId": 1,
    "answers": [
      {"questionId": 1, "answerId": 4},
      {"questionId": 2, "answerId": 5}
    ]
  }'

# Get evaluation analytics
curl -X GET http://localhost:8760/api/v1/evaluation/analytics/course/1/lecturer/1 \
  -H "X-User-Role: ADMIN"
```

## 📊 Database Schema

### Evaluation Tables

- `evaluation_categories`: Question categories
- `evaluation_questions`: Question bank organized by category
- `evaluation_options`: Answer options (1-5 scale)
- `evaluation_sessions`: Session management for course evaluations
- `evaluations`: Student evaluations of lecturers
- `evaluation_answers`: Individual question responses

## 🔐 Security Features

- **Role-based Access**: Different permissions for students, lecturers, and admins
- **Anonymous Evaluations**: Student identities protected in analytics
- **Department-level Isolation**: Admins can only access their department's data
- **Session-based Control**: Evaluations only available during active sessions

## 📈 Analytics Features

- **Overall Ratings**: Aggregate scores across all questions
- **Category Breakdowns**: Performance by teaching aspect
- **Question-level Analysis**: Detailed feedback on specific areas
- **Rating Distributions**: Visual representation of score spreads
- **Comparative Analysis**: Performance across courses and semesters
- **Department-wide Insights**: Identify trends and outliers

## 🔄 Integration Points

- **Course Service**: Validates course sessions and enrollments
- **Frontend**: Provides interactive charts and visualizations
- **Reporting System**: Generates PDF reports for administrative review