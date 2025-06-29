# CAMS - Comprehensive API Documentation

This document provides a detailed overview of all API endpoints available in the College Academics Management System (CAMS). The documentation is organized by service to help developers understand the functionality and integration points.

## Table of Contents

- [Authentication Service](#authentication-service)
- [User Service](#user-service)
- [Course Service](#course-service)
- [Grade Service](#grade-service)
- [Resource Service](#resource-service)
- [Communication Service](#communication-service)

---

## Authentication Service

Base URL: `http://localhost:8762/api/auth`

The Authentication Service handles user authentication and JWT token management.

### Login

Authenticates a user and returns a JWT token.

**Endpoint:** `POST /login`

**Headers:**
- `Content-Type: application/json`

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

---

## User Service

Base URL: `http://localhost:8763/api/users`

The User Service manages user accounts, profiles, and the evaluation system.

### User Management

#### Get All Users

Retrieves a list of all users in the system.

**Endpoint:** `GET /`

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

Creates a new user account.

**Endpoint:** `POST /`

**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "role": "STUDENT",
  "isVerified": false
}
```

#### Delete User

Deletes a user account.

**Endpoint:** `DELETE /{id}`

**Headers:**
- `X-User-Role`: ADMIN

**Response:**
- `204 No Content` - User deleted successfully

#### Validate Credentials

Validates user credentials for authentication.

**Endpoint:** `GET /validate`

**Query Parameters:**
- `email`: User email
- `password`: User password

**Response:**
```json
true
```

#### Get User by Email

Retrieves user details by email address.

**Endpoint:** `GET /email/{email}`

**Response:**
```json
{
  "id": 1,
  "departmentId": 1,
  "email": "john.doe@example.com",
  "password": "[HASHED_PASSWORD]",
  "firstname": "John",
  "lastname": "Doe",
  "role": "STUDENT",
  "profileImage": null,
  "isVerified": true
}
```

### Lecturer Management

#### Get Lecturer by ID

Retrieves lecturer details by ID.

**Endpoint:** `GET /lecturer/{lecturerId}`

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

Retrieves lecturer details by user ID.

**Endpoint:** `GET /lecturer/user/{userId}`

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

Creates a new evaluation session for a course.

**Endpoint:** `POST /v1/evaluation/session`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

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

Activates an evaluation session to allow student submissions.

**Endpoint:** `POST /v1/evaluation/session/{sessionId}/activate`

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

Checks if an evaluation session is active.

**Endpoint:** `GET /v1/evaluation/session/{sessionId}/status`

**Response:**
```json
{
  "success": true,
  "message": "Evaluation Session is Active"
}
```

#### Get Evaluation Sessions by Department

Retrieves all evaluation sessions for a department.

**Endpoint:** `GET /v1/evaluation/sessions/department/{departmentId}`

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

Submits a student evaluation for a lecturer.

**Endpoint:** `POST /v1/evaluation/submit`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT
- `Content-Type: application/json`

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

Retrieves all evaluation questions.

**Endpoint:** `GET /v1/evaluation/questions`

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

Retrieves evaluation questions by category.

**Endpoint:** `GET /v1/evaluation/questions/category/{categoryId}`

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

Retrieves all evaluation categories.

**Endpoint:** `GET /v1/evaluation/categories`

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

Retrieves evaluation analytics for a specific course and lecturer.

**Endpoint:** `GET /v1/evaluation/analytics/course/{courseSessionId}/lecturer/{lecturerId}`

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

Retrieves evaluation analytics for a lecturer across all courses.

**Endpoint:** `GET /v1/evaluation/analytics/lecturer/{lecturerId}`

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
  }
]
```

#### Get Department Evaluation Analytics

Retrieves evaluation analytics for all lecturers in a department.

**Endpoint:** `GET /v1/evaluation/analytics/department/{departmentId}`

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
  }
]
```

---

## Course Service

Base URL: `http://localhost:8764/api`

The Course Service manages courses, batches, course sessions, enrollments, and lecturer assignments.

### Course Management

#### Create Course

Creates a new course.

**Endpoint:** `POST /v1/courses`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

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

Retrieves a course by ID.

**Endpoint:** `GET /v1/courses/{id}`

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

Retrieves all courses.

**Endpoint:** `GET /v1/courses`

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

Creates a new student batch.

**Endpoint:** `POST /batches`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

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

#### Update Batch

Updates an existing batch.

**Endpoint:** `PUT /batches/{id}`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

**Request Body:** Same as create batch

#### Delete Batch

Deletes a batch.

**Endpoint:** `DELETE /batches/{id}`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Get Batch by ID

Retrieves a batch by ID.

**Endpoint:** `GET /batches/{id}`

#### Get Batches by Department

Retrieves all batches for a department.

**Endpoint:** `GET /batches/department/{departmentId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Assign Courses to Batch

Assigns courses to a batch.

**Endpoint:** `POST /batches/course-assignments`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

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

#### Get Course Assignments for Batch

Retrieves all course assignments for a batch.

**Endpoint:** `GET /batches/{batchId}/course-assignments`

#### Remove Course Assignment

Removes a course assignment from a batch.

**Endpoint:** `DELETE /batches/course-assignments/{assignmentId}`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Advance Batch Semester

Advances a batch to the next semester.

**Endpoint:** `POST /batches/{batchId}/advance-semester`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

### Course Session Management

#### Create Course Session

Creates a new course session.

**Endpoint:** `POST /course-sessions`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

**Request Body:**
```json
{
  "academicYear": 2024,
  "semester": 1,
  "year": 1,
  "courseId": 1,
  "departmentId": 1,
  "lecturerIds": [123, 456],
  "batchId": 1
}
```

#### Update Course Session

Updates an existing course session.

**Endpoint:** `PUT /course-sessions/{id}`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

**Request Body:** Same as create course session

#### Delete Course Session

Deletes a course session.

**Endpoint:** `DELETE /course-sessions/{id}`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Get Course Session by ID

Retrieves a course session by ID.

**Endpoint:** `GET /course-sessions/{id}`

#### Get Course Sessions by Department

Retrieves all course sessions for a department.

**Endpoint:** `GET /course-sessions/department/{departmentId}`

#### Get Course Sessions by Batch

Retrieves all course sessions for a batch.

**Endpoint:** `GET /course-sessions/batch/{batchId}`

#### Get Course Sessions by Batch, Year, and Semester

Retrieves course sessions for a specific batch, year, and semester.

**Endpoint:** `GET /course-sessions/batch/{batchId}/year/{year}/semester/{semester}`

#### Activate Course Session

Activates a course session.

**Endpoint:** `POST /course-sessions/{id}/activate`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Deactivate Course Session

Deactivates a course session.

**Endpoint:** `POST /course-sessions/{id}/deactivate`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Open Enrollment

Opens enrollment for a course session.

**Endpoint:** `POST /course-sessions/{id}/open-enrollment`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Close Enrollment

Closes enrollment for a course session.

**Endpoint:** `POST /course-sessions/{id}/close-enrollment`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

### Enrollment Management

#### Enroll Student

Enrolls a student in a course session.

**Endpoint:** `POST /enrollment/enroll`

**Headers:**
- `X-User-Id`: User ID (must match studentId for STUDENT role)
- `X-User-Role`: STUDENT, ADMIN, SUPER_ADMIN

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

#### Unenroll Student

Unenrolls a student from a course session.

**Endpoint:** `DELETE /enrollment/unenroll`

**Headers:**
- `X-User-Id`: User ID (must match studentId for STUDENT role)
- `X-User-Role`: STUDENT, ADMIN, SUPER_ADMIN

**Query Parameters:**
- `studentId`: Student ID
- `courseSessionId`: Course session ID

#### Check Enrollment

Checks if a student is enrolled in a course session.

**Endpoint:** `GET /enrollment/check-enrollment`

**Query Parameters:**
- `studentId`: Student ID
- `courseSessionId`: Course session ID

**Response:**
```json
true
```

#### Get Enrolled Students

Retrieves all students enrolled in a course session.

**Endpoint:** `GET /enrollment/course-session/{courseSessionId}/students`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN, LECTURER

**Response:**
```json
[456, 789, 101]
```

#### Get Student Course Sessions

Retrieves all course sessions for a student.

**Endpoint:** `GET /enrollment/student/{studentId}`

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

### Lecturer Management

#### Set Lecturer Capacity

Sets the maximum credit hours a lecturer can teach.

**Endpoint:** `POST /lecturer-management/capacity`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

**Request Body:**
```json
{
  "lecturerId": 123,
  "departmentId": 1,
  "maxCreditHours": 12
}
```

#### Get Lecturer Capacity

Retrieves a lecturer's teaching capacity.

**Endpoint:** `GET /lecturer-management/capacity/{lecturerId}`

#### Get Lecturer Capacities by Department

Retrieves teaching capacities for all lecturers in a department.

**Endpoint:** `GET /lecturer-management/capacity/department/{departmentId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN, LECTURER

#### Assign Teachable Courses

Assigns courses that a lecturer can teach.

**Endpoint:** `POST /lecturer-management/teachable-courses`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

**Request Body:**
```json
{
  "lecturerId": 123,
  "courseIds": [1, 2, 3]
}
```

#### Get Teachable Courses

Retrieves courses that a lecturer can teach.

**Endpoint:** `GET /lecturer-management/teachable-courses/{lecturerId}`

#### Validate Lecturer for Course Session

Validates if a lecturer is assigned to a course session.

**Endpoint:** `GET /assignment/lecturer/{lecturerId}/validate/{courseSessionId}`

**Response:**
```json
true
```

---

## Grade Service

Base URL: `http://localhost:8766/api/grades`

The Grade Service manages assessments, assignments, grading, and academic performance tracking.

### Assignment Management

#### Create Assignment

Creates a new assignment.

**Endpoint:** `POST /assignments`

**Content-Type:** `multipart/form-data`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Form Data:**
- `title`: Assignment title
- `description`: Assignment description
- `courseSessionId`: Course session ID
- `dueDate`: Due date (ISO format: 2024-04-15T23:59:59)
- `maxScore`: Maximum score (integer)
- `type`: INDIVIDUAL or GROUP
- `files`: Optional file attachments

**Response:**
```json
{
  "id": 1,
  "title": "Programming Assignment 2",
  "description": "Implement a binary search tree with operations",
  "courseSessionId": 1,
  "lecturerId": 123,
  "lecturerName": "Dr. Jane Smith",
  "dueDate": "2024-04-15T23:59:59",
  "createdAt": "2024-03-01T10:00:00",
  "maxScore": 100,
  "type": "INDIVIDUAL",
  "status": "DRAFT",
  "attachmentIds": [1, 2],
  "attachments": [
    {
      "id": 1,
      "title": "Assignment Instructions",
      "fileName": "instructions.pdf",
      "downloadUrl": "/api/v1/resources/download/uuid-filename.pdf/1"
    }
  ],
  "submissionCount": 0,
  "isOverdue": false
}
```

#### Update Assignment

Updates an existing assignment.

**Endpoint:** `PUT /assignments/{id}`

**Headers:**
- `X-User-Id`: Lecturer ID (must be owner)
- `X-User-Role`: LECTURER
- `Content-Type: application/json`

**Request Body:**
```json
{
  "title": "Updated Assignment Title",
  "description": "Updated description",
  "dueDate": "2024-04-20T23:59:59",
  "maxScore": 100,
  "type": "INDIVIDUAL"
}
```

#### Publish Assignment

Publishes an assignment to make it visible to students.

**Endpoint:** `POST /assignments/{id}/publish`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

#### Get Assignment Details

Retrieves assignment details.

**Endpoint:** `GET /assignments/{id}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

#### Get Assignments by Course Session

Retrieves all assignments for a course session.

**Endpoint:** `GET /assignments/course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Get My Assignments (Lecturer)

Retrieves all assignments created by a lecturer.

**Endpoint:** `GET /assignments/my-assignments`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

### Assignment Submission

#### Submit Assignment

Submits an assignment.

**Endpoint:** `POST /assignments/submit`

**Content-Type:** `multipart/form-data`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Form Data:**
- `assignmentId`: Assignment ID
- `content`: Optional text content
- `files`: File attachments

**Response:**
```json
{
  "id": 1,
  "assignmentId": 1,
  "assignmentTitle": "Programming Assignment 2",
  "studentId": 456,
  "studentName": "John Doe",
  "content": "My solution explanation...",
  "attachmentIds": [3, 4],
  "attachments": [
    {
      "id": 3,
      "title": "solution.py",
      "fileName": "uuid-solution.py",
      "downloadUrl": "/api/v1/resources/download/uuid-solution.py/3"
    }
  ],
  "submittedAt": "2024-04-10T15:30:00",
  "status": "SUBMITTED",
  "score": null,
  "maxScore": 100,
  "feedback": null,
  "isLate": false
}
```

#### Get My Submissions

Retrieves all submissions by a student.

**Endpoint:** `GET /assignments/submissions/my-submissions`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

#### Get My Submission for Assignment

Retrieves a student's submission for a specific assignment.

**Endpoint:** `GET /assignments/{assignmentId}/my-submission`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

### Grading System

#### Create Grade Type

Creates a new grade type.

**Endpoint:** `POST /grading/grade-types`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER
- `Content-Type: application/json`

**Request Body:**
```json
{
  "name": "Quiz 1",
  "description": "First quiz on data structures",
  "maxScore": 20,
  "weightPercentage": 10.0,
  "courseSessionId": 1,
  "category": "QUIZ"
}
```

#### Create Default Grade Types

Creates default grade types for a course session.

**Endpoint:** `POST /grading/grade-types/course-session/{courseSessionId}/defaults`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

#### Get Grade Types by Course Session

Retrieves all grade types for a course session.

**Endpoint:** `GET /grading/grade-types/course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Add/Update Student Grade

Adds or updates a student's grade.

**Endpoint:** `POST /grading/grades`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER
- `Content-Type: application/json`

**Request Body:**
```json
{
  "studentId": 456,
  "gradeTypeId": 1,
  "score": 18.0,
  "feedback": "Excellent work on data structures!"
}
```

#### Get Gradebook

Retrieves the gradebook for a course session.

**Endpoint:** `GET /grading/gradebook/course-session/{courseSessionId}`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

#### Grade Assignment Submission

Grades a student's assignment submission.

**Endpoint:** `POST /assignments/submissions/{submissionId}/grade`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER
- `Content-Type: application/json`

**Request Body:**
```json
{
  "score": 85.0,
  "feedback": "Good implementation! Consider adding more error handling."
}
```

#### Get Assignment Submissions

Retrieves all submissions for an assignment.

**Endpoint:** `GET /assignments/{assignmentId}/submissions`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

### Student Assessment Overview

#### Get My Assessment Overview

Retrieves a student's assessment overview for a course session.

**Endpoint:** `GET /grading/my-assessments/course-session/{courseSessionId}`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
{
  "courseSessionId": 1,
  "courseCode": "CS101",
  "courseName": "Programming Fundamentals",
  "assessments": [
    {
      "id": 1,
      "title": "Programming Assignment 2",
      "description": "Implement a binary search tree",
      "type": "ASSIGNMENT",
      "dueDate": "2024-04-15T23:59:59",
      "maxScore": 100,
      "status": "submitted",
      "score": 85.0,
      "feedback": "Good work! Consider adding more error handling.",
      "isLate": false,
      "isOverdue": false,
      "attachments": [
        {
          "id": 1,
          "title": "Instructions",
          "fileName": "instructions.pdf",
          "downloadUrl": "/api/v1/resources/download/uuid-filename.pdf/1"
        }
      ],
      "gradeDisplay": "85/100"
    }
  ],
  "totalAssessments": 8,
  "completedAssessments": 5,
  "pendingAssessments": 3,
  "overallGrade": 87.5,
  "overallLetterGrade": "B"
}
```

#### Get Student Assignments

Retrieves all assignments for a student.

**Endpoint:** `GET /assignments/student`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

---

## Resource Service

Base URL: `http://localhost:8767/api/v1/resources`

The Resource Service manages file uploads, downloads, and resource sharing.

### File Resource Management

#### Upload File Resource

Uploads a file resource.

**Endpoint:** `POST /`

**Content-Type:** `multipart/form-data`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Form Data:**
- `file`: File to upload
- `title`: Resource title
- `description`: Resource description
- `type`: Resource type (DOCUMENT, VIDEO, PHOTO, AUDIO, ARCHIVE)
- `courseSessionId`: Course session ID
- `categories`: List of categories

**Response:**
```json
{
  "id": 1,
  "title": "Lecture Notes - Chapter 1",
  "description": "Introduction to programming concepts",
  "fileName": "uuid-generated-filename.pdf",
  "originalFileName": "chapter1-notes.pdf",
  "type": "DOCUMENT",
  "fileSize": 2048576,
  "mimeType": "application/pdf",
  "categories": ["lecture-notes", "programming"],
  "downloadCount": 0,
  "uploadedAt": "2024-01-15T10:30:00",
  "courseSessionId": 1,
  "uploadedBy": 123,
  "uploaderName": "Dr. Jane Smith",
  "status": "ACTIVE",
  "downloadUrl": "/api/v1/resources/download/uuid-generated-filename.pdf/1",
  "fileSizeFormatted": "2.0 MB"
}
```

#### Create Link Resource

Creates a link resource.

**Endpoint:** `POST /link`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER
- `Content-Type: application/json`

**Request Body:**
```json
{
  "title": "External Tutorial",
  "description": "Helpful programming tutorial",
  "type": "LINK",
  "courseSessionId": 1,
  "categories": ["tutorial", "external"],
  "linkUrl": "https://example.com/tutorial"
}
```

### Resource Retrieval

#### Get Resource by ID

Retrieves a resource by ID.

**Endpoint:** `GET /{id}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER, ADMIN

#### Get Resources by Course Session

Retrieves all resources for a course session.

**Endpoint:** `GET /course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Get Resources by Type

Retrieves resources by type for a course session.

**Endpoint:** `GET /course-session/{courseSessionId}/type/{type}`

**Path Parameters:**
- `type`: DOCUMENT, VIDEO, PHOTO, LINK, AUDIO, ARCHIVE

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Get Resources by Category

Retrieves resources by category for a course session.

**Endpoint:** `GET /course-session/{courseSessionId}/category/{category}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Search Resources

Searches resources by title or description.

**Endpoint:** `GET /course-session/{courseSessionId}/search`

**Query Parameters:**
- `searchTerm`: Search term for title or description

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

### File Download

#### Download Resource

Downloads a resource file.

**Endpoint:** `GET /download/{fileName}/{id}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

**Response:**
- File download with appropriate headers

---

## Communication Service

Base URL: `http://localhost:8765/api/com`

The Communication Service manages announcements, notifications, support tickets, and chat functionality.

### Announcements

#### Create Announcement

Creates a new announcement.

**Endpoint:** `POST /announcements`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `X-User-Id`: Admin ID
- `X-User-Department`: Department code
- `Content-Type: application/json`

**Request Body:**
```json
{
  "title": "Important Academic Update",
  "content": "Please note the changes in the academic calendar...",
  "category": "ACADEMIC"
}
```

#### Get Announcements for User

Retrieves announcements for a user.

**Endpoint:** `GET /announcements`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID
- `X-User-Department`: Department code

#### Get Announcements by Category

Retrieves announcements by category.

**Endpoint:** `GET /announcements/category/{category}`

**Path Parameters:**
- `category`: ACADEMIC, ADMINISTRATIVE

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID
- `X-User-Department`: Department code

#### Get Unread Count

Retrieves the count of unread announcements.

**Endpoint:** `GET /announcements/unread-count`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID
- `X-User-Department`: Department code

#### Mark as Read

Marks an announcement as read.

**Endpoint:** `POST /announcements/{id}/mark-read`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID

### Notifications

#### Create Notification (Lecturer)

Creates a new notification for students.

**Endpoint:** `POST /notifications`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER
- `Content-Type: application/json`

**Request Body:**
```json
{
  "subject": "Assignment Deadline Reminder",
  "message": "Don't forget to submit your assignment by Friday",
  "type": "deadline",
  "courseSessionId": 1
}
```

#### Get Notifications for Student

Retrieves notifications for a student.

**Endpoint:** `GET /notifications/student`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

#### Get Unread Notification Count

Retrieves the count of unread notifications.

**Endpoint:** `GET /notifications/student/unread-count`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

#### Mark Notification as Read

Marks a notification as read.

**Endpoint:** `POST /notifications/{notificationId}/mark-read`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

### Support Tickets

#### Create Ticket

Creates a new support ticket.

**Endpoint:** `POST /tickets`

**Headers:**
- `X-User-Id`: Student/Lecturer ID
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Department`: Department code
- `Content-Type: application/json`

**Request Body:**
```json
{
  "subject": "Grade Inquiry",
  "message": "I have a question about my midterm grade...",
  "priority": "MEDIUM"
}
```

#### Get My Tickets

Retrieves tickets created by the user.

**Endpoint:** `GET /tickets/my-tickets`

**Headers:**
- `X-User-Id`: User ID

#### Get All Tickets (Admin)

Retrieves all tickets for admin review.

**Endpoint:** `GET /tickets/admin`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

#### Respond to Ticket (Admin)

Responds to a support ticket.

**Endpoint:** `POST /tickets/{ticketId}/respond`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `Content-Type: application/json`

**Request Body:**
```json
{
  "message": "Thank you for your inquiry. I'll look into this matter...",
  "newStatus": "IN_PROGRESS"
}
```

### Chat System

#### Create/Get Chat Room

Creates or retrieves a chat room between a student and lecturer.

**Endpoint:** `POST /chat/rooms`

**Query Parameters:**
- `courseSessionId`: 1
- `studentId`: 123
- `studentName`: John Doe
- `lecturerId`: 456
- `lecturerName`: Dr. Jane Smith

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
Content-Type: application/json
```

#### Get User's Chat Rooms

Retrieves all chat rooms for a user.

**Endpoint:** `GET /chat/rooms`

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
```

#### Send Message

Sends a message in a chat room.

**Endpoint:** `POST /chat/messages`

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
Content-Type: application/json
```

**Request Body:**
```json
{
  "roomId": "1_123_456",
  "content": "Hello! I have a question about the assignment.",
  "messageType": "TEXT"
}
```

#### Get Chat History

Retrieves chat history for a room.

**Endpoint:** `GET /chat/rooms/{roomId}/messages`

**Path Variables:**
- `roomId`: 1_123_456

**Query Parameters:**
- `page`: 0
- `size`: 20

**Headers:**
```
X-User-Id: 123
```

#### Mark Messages as Read

Marks all messages in a room as read.

**Endpoint:** `POST /chat/rooms/{roomId}/mark-read`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
```

#### Get Unread Message Count

Retrieves the count of unread messages in a room.

**Endpoint:** `GET /chat/rooms/{roomId}/unread-count`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
```

#### WebSocket Connection

Establishes a WebSocket connection for real-time chat.

**Connection URL:**
```
ws://localhost:8765/ws/chat?userId=123&userName=John&userRole=STUDENT&roomId=1_123_456
```

**WebSocket Message Types:**
- `SEND_MESSAGE`: Send a message
- `JOIN_ROOM`: Join a chat room
- `LEAVE_ROOM`: Leave a chat room
- `TYPING`: Send typing indicator
- `MARK_READ`: Mark messages as read

---

## Common Headers for All Services

All API requests (except auth endpoints) require the following headers:

- `Authorization: Bearer <jwt-token>` - JWT authentication token
- `X-User-Id` - User ID extracted from JWT token
- `X-User-Role` - User role (STUDENT, LECTURER, ADMIN, SUPER_ADMIN)
- `X-User-Department` - Department code (when applicable)

These headers are automatically added by the API Gateway after successful authentication.

## Error Handling

All services follow a consistent error response format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "timestamp": "2024-03-15T10:30:00Z",
  "path": "/api/endpoint"
}
```

Common HTTP status codes:

- `200 OK` - Successful operation
- `201 Created` - Resource created successfully
- `204 No Content` - Successful deletion
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict
- `500 Internal Server Error` - System error

## Testing with Postman

1. **Authentication:**
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

2. **Course Management:**
   ```bash
   # Create a batch
   curl -X POST http://localhost:8760/api/batches \
     -H "Content-Type: application/json" \
     -H "X-User-Id: 123" \
     -H "X-User-Role: ADMIN" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
       "name": "CS-2024-Batch-1",
       "admissionYear": 2024,
       "currentYear": 1,
       "currentSemester": 1,
       "departmentId": 1
     }'
   ```

3. **Resource Upload:**
   ```bash
   # Upload a file resource
   curl -X POST http://localhost:8760/api/v1/resources \
     -H "X-User-Id: 123" \
     -H "X-User-Role: LECTURER" \
     -H "Authorization: Bearer $TOKEN" \
     -F "file=@/path/to/document.pdf" \
     -F "title=Lecture Notes" \
     -F "description=Chapter 1 notes" \
     -F "type=DOCUMENT" \
     -F "courseSessionId=1" \
     -F "categories=lecture-notes" \
     -F "categories=programming"
   ```

4. **Evaluation System:**
   ```bash
   # Create evaluation session
   curl -X POST http://localhost:8760/api/v1/evaluation/session \
     -H "Content-Type: application/json" \
     -H "X-User-Id: 123" \
     -H "X-User-Role: ADMIN" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
       "courseSessionId": 1,
       "startDate": "2024-05-01T00:00:00",
       "endDate": "2024-05-15T23:59:59",
       "departmentId": 1
     }'
   ```

## Integration Patterns

1. **User Authentication:**
   - Login via Auth Service to get JWT token
   - Include token in all subsequent requests
   - API Gateway validates token and adds user context headers

2. **Course Enrollment:**
   - Admin creates course sessions
   - Admin opens enrollment
   - Students enroll in courses
   - System validates enrollment for grade submissions

3. **Resource Sharing:**
   - Lecturers upload resources to Resource Service
   - Resources are associated with course sessions
   - Students access resources based on enrollment

4. **Evaluation Workflow:**
   - Admin creates evaluation session
   - Admin activates session
   - Students submit evaluations
   - Admin views analytics

This comprehensive API documentation provides a detailed overview of all available endpoints in the CAMS system, their functionality, request/response formats, and integration patterns. Use this as a reference for frontend development and system integration.