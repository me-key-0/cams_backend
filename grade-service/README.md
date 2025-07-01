# Grade Service

The Grade Service manages assessments, assignments, grading, and academic performance tracking for the CAMS system. It provides comprehensive functionality for lecturers to create assignments, manage grades, and track student progress, while enabling students to view their assessments and academic performance.

## 🎯 Purpose

- **Assignment Management**: Create, publish, and manage assignments with file attachments
- **Submission Handling**: Process student submissions with file uploads and status tracking
- **Grading System**: Comprehensive grading with multiple grade types and weighted calculations
- **Group Management**: Create and manage student groups for collaborative assignments
- **Academic Analytics**: Generate gradebooks, export data, and track performance metrics
- **Student Portal**: Unified assessment view for students with progress tracking

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- Resource Service running (for file attachments)
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

### 🎯 Assignment Management (Lecturer)

#### Create Assignment
**POST** `/api/grades/assignments`

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
**PUT** `/api/grades/assignments/{id}`

**Headers:**
- `X-User-Id`: Lecturer ID (must be owner)
- `X-User-Role`: LECTURER

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
**POST** `/api/grades/assignments/{id}/publish`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

#### Get Assignment Details
**GET** `/api/grades/assignments/{id}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

#### Get Assignments by Course Session
**GET** `/api/grades/assignments/course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Get My Assignments (Lecturer)
**GET** `/api/grades/assignments/my-assignments`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

#### Delete Assignment
**DELETE** `/api/grades/assignments/{id}`

**Headers:**
- `X-User-Id`: Lecturer ID (must be owner)
- `X-User-Role`: LECTURER

### 📝 Assignment Submission (Student)

#### Submit Assignment
**POST** `/api/grades/assignments/submit`

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

#### Update Submission
**PUT** `/api/grades/assignments/submissions/{submissionId}`

**Headers:**
- `X-User-Id`: Student ID (must be owner)
- `X-User-Role`: STUDENT

#### Get My Submissions
**GET** `/api/grades/assignments/submissions/my-submissions`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

#### Get My Submission for Assignment
**GET** `/api/grades/assignments/{assignmentId}/my-submission`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

### 📊 Grading System (Lecturer)

#### Create Grade Type
**POST** `/api/grades/grading/grade-types`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

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

**Response:**
```json
{
  "id": 1,
  "name": "Quiz 1",
  "description": "First quiz on data structures",
  "maxScore": 20,
  "weightPercentage": 10.0,
  "courseSessionId": 1,
  "createdBy": 123,
  "createdAt": "2024-03-01T10:00:00",
  "category": "QUIZ",
  "isDefault": false,
  "isActive": true
}
```

#### Create Default Grade Types
**POST** `/api/grades/grading/grade-types/course-session/{courseSessionId}/defaults`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

*Creates default grade types: Midterm Exam (30%), Final Exam (40%), Assignments (20%), Participation (10%)*

#### Get Grade Types by Course Session
**GET** `/api/grades/grading/grade-types/course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Add/Update Student Grade
**POST** `/api/grades/grading/grades`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Request Body:**
```json
{
  "studentId": 456,
  "gradeTypeId": 1,
  "score": 18.0,
  "feedback": "Excellent work on data structures!"
}
```

#### Add Bulk Grades
**POST** `/api/grades/grading/grades/bulk`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Request Body:**
```json
{
  "gradeTypeId": 1,
  "feedback": "Good performance overall",
  "grades": [
    {
      "studentId": 456,
      "score": 18.0
    },
    {
      "studentId": 457,
      "score": 16.5
    }
  ]
}
```

#### Get Gradebook
**GET** `/api/grades/grading/gradebook/course-session/{courseSessionId}`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Response:**
```json
{
  "courseSessionId": 1,
  "courseCode": "CS101",
  "courseName": "Programming Fundamentals",
  "gradeTypes": [
    {
      "id": 1,
      "name": "Midterm Exam",
      "maxScore": 100,
      "weightPercentage": 30.0
    }
  ],
  "students": [
    {
      "studentId": 456,
      "studentName": "John Doe",
      "studentEmail": "john.doe@example.com",
      "grades": {
        "1": {
          "score": 85.0,
          "feedback": "Good work!"
        }
      },
      "finalGrade": 87.5,
      "letterGrade": "B"
    }
  ],
  "classAverages": {
    "Midterm Exam": 82.5,
    "Final Exam": 78.0
  }
}
```

#### Export Gradebook to Excel
**GET** `/api/grades/grading/gradebook/course-session/{courseSessionId}/export`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Response:** Excel file download

#### Import Grades from Excel
**POST** `/api/grades/grading/gradebook/course-session/{courseSessionId}/import`

**Content-Type:** `multipart/form-data`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Form Data:**
- `file`: Excel file
- `gradeTypeId`: Grade type ID to import grades for

#### Grade Assignment Submission
**POST** `/api/grades/assignments/submissions/{submissionId}/grade`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Request Body:**
```json
{
  "score": 85.0,
  "feedback": "Good implementation! Consider adding more error handling."
}
```

#### Get Assignment Submissions
**GET** `/api/grades/assignments/{assignmentId}/submissions`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

### 👥 Group Management

#### Create Group
**POST** `/api/grades/groups`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

**Request Body:**
```json
{
  "name": "Team Alpha",
  "description": "Group for web development project",
  "courseSessionId": 1,
  "type": "STUDENT_CREATED",
  "members": [
    {
      "studentId": 456,
      "studentName": "John Doe",
      "role": "LEADER"
    },
    {
      "studentId": 457,
      "studentName": "Jane Smith",
      "role": "MEMBER"
    }
  ]
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Team Alpha",
  "description": "Group for web development project",
  "courseSessionId": 1,
  "createdBy": 456,
  "createdByName": "John Doe",
  "type": "STUDENT_CREATED",
  "createdAt": "2024-03-01T10:00:00",
  "isActive": true,
  "members": [
    {
      "id": 1,
      "studentId": 456,
      "studentName": "John Doe",
      "role": "LEADER",
      "joinedAt": "2024-03-01T10:00:00"
    }
  ],
  "memberCount": 2
}
```

#### Get Groups by Course Session
**GET** `/api/grades/groups/course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

#### Add Member to Group
**POST** `/api/grades/groups/{groupId}/members`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

**Request Body:**
```json
{
  "studentId": 458,
  "studentName": "Bob Johnson",
  "role": "MEMBER"
}
```

#### Assign Group Grade
**POST** `/api/grades/groups/{groupId}/grade`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Query Parameters:**
- `gradeTypeId`: Grade type ID
- `score`: Score value
- `feedback`: Optional feedback

### 📚 Student Assessment Overview

#### Get My Assessment Overview
**GET** `/api/grades/grading/my-assessments/course-session/{courseSessionId}`

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
    },
    {
      "id": 2,
      "title": "Midterm Exam",
      "description": "Covers chapters 1-5",
      "type": "EXAM",
      "dueDate": null,
      "maxScore": 100,
      "status": "graded",
      "score": 87.0,
      "feedback": "Good work! Review chapter 3 for final exam.",
      "isLate": false,
      "isOverdue": false,
      "attachments": [],
      "gradeDisplay": "87/100"
    },
    {
      "id": 3,
      "title": "Final Exam",
      "description": "Comprehensive final examination",
      "type": "EXAM",
      "dueDate": "2024-05-20T14:00:00",
      "maxScore": 100,
      "status": "pending",
      "score": null,
      "feedback": null,
      "isLate": false,
      "isOverdue": false,
      "attachments": [],
      "gradeDisplay": "pending"
    }
  ],
  "totalAssessments": 8,
  "completedAssessments": 5,
  "pendingAssessments": 3,
  "overallGrade": 87.5,
  "overallLetterGrade": "B"
}
```

#### Get My Assessment List
**GET** `/api/grades/grading/my-assessments/course-session/{courseSessionId}/list`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

#### Get Student Assignments
**GET** `/api/grades/assignments/student`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
[
  {
    "id": 1,
    "title": "Programming Assignment 2",
    "description": "Implement a binary search tree",
    "dueDate": "2024-04-15T23:59:59",
    "maxScore": 100,
    "type": "INDIVIDUAL",
    "attachments": [
      {
        "id": 1,
        "title": "Instructions",
        "fileName": "instructions.pdf",
        "downloadUrl": "/api/v1/resources/download/uuid-filename.pdf/1"
      }
    ],
    "submissionStatus": "SUBMITTED",
    "submittedAt": "2024-04-10T15:30:00",
    "score": 85.0,
    "feedback": "Good work!",
    "isLate": false,
    "isOverdue": false,
    "statusDisplay": "submitted",
    "gradeDisplay": "85/100"
  }
]
```

## 🏗️ Data Models

### Core Entities

#### Assignment
```typescript
interface Assignment {
  id: number;
  title: string;
  description: string;
  courseSessionId: number;
  lecturerId: number;
  lecturerName: string;
  dueDate: string; // ISO datetime
  createdAt: string; // ISO datetime
  maxScore: number;
  type: 'INDIVIDUAL' | 'GROUP';
  status: 'DRAFT' | 'PUBLISHED' | 'CLOSED';
  attachmentIds: number[];
  isGroupAssignment: boolean;
}
```

#### Submission
```typescript
interface Submission {
  id: number;
  assignmentId: number;
  studentId: number;
  studentName: string;
  content: string;
  attachmentIds: number[];
  submittedAt: string; // ISO datetime
  status: 'PENDING' | 'SUBMITTED' | 'GRADED' | 'RETURNED';
  score: number | null;
  maxScore: number;
  feedback: string | null;
  isLate: boolean;
  gradedAt: string | null; // ISO datetime
  gradedBy: number | null;
  groupId: number | null;
}
```

#### GradeType
```typescript
interface GradeType {
  id: number;
  name: string;
  description: string;
  maxScore: number;
  weightPercentage: number;
  courseSessionId: number;
  createdBy: number;
  createdAt: string; // ISO datetime
  category: 'EXAM' | 'QUIZ' | 'ASSIGNMENT' | 'PROJECT' | 'PARTICIPATION' | 'OTHER';
  isDefault: boolean;
  isActive: boolean;
  assignmentId: number | null;
}
```

#### StudentGrade
```typescript
interface StudentGrade {
  id: number;
  studentId: number;
  studentName: string;
  gradeTypeId: number;
  gradeTypeName: string;
  maxScore: number;
  score: number;
  feedback: string | null;
  gradedBy: number;
  graderName: string;
  gradedAt: string; // ISO datetime
  groupId: number | null;
}
```

#### StudentGroup
```typescript
interface StudentGroup {
  id: number;
  name: string;
  description: string;
  courseSessionId: number;
  createdBy: number;
  createdByName: string;
  type: 'LECTURER_CREATED' | 'STUDENT_CREATED';
  createdAt: string; // ISO datetime
  isActive: boolean;
  members: GroupMember[];
  memberCount: number;
}
```

## 🎯 Frontend Integration Guide

### 📱 Student Assessment Page Implementation

```typescript
// Fetch student's complete assessment overview
const fetchStudentAssessments = async (courseSessionId: number) => {
  const response = await fetch(
    `/api/grades/grading/my-assessments/course-session/${courseSessionId}`,
    {
      headers: {
        'X-User-Id': studentId,
        'X-User-Role': 'STUDENT',
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.json();
};

// Display assessment status with appropriate styling
const getStatusColor = (status: string) => {
  switch (status) {
    case 'graded': return 'text-green-600';
    case 'submitted': return 'text-blue-600';
    case 'pending': return 'text-orange-600';
    default: return 'text-gray-600';
  }
};
```

### 📊 Lecturer Gradebook Implementation

```typescript
// Fetch gradebook data
const fetchGradebook = async (courseSessionId: number) => {
  const response = await fetch(
    `/api/grades/grading/gradebook/course-session/${courseSessionId}`,
    {
      headers: {
        'X-User-Id': lecturerId,
        'X-User-Role': 'LECTURER',
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.json();
};

// Add/update grade
const updateGrade = async (studentId: number, gradeTypeId: number, score: number, feedback?: string) => {
  const response = await fetch('/api/grades/grading/grades', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': lecturerId,
      'X-User-Role': 'LECTURER',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      studentId,
      gradeTypeId,
      score,
      feedback
    })
  });
  return response.json();
};
```

### 📝 Assignment Submission Flow

```typescript
// Submit assignment with files
const submitAssignment = async (assignmentId: number, content: string, files: File[]) => {
  const formData = new FormData();
  formData.append('assignmentId', assignmentId.toString());
  formData.append('content', content);
  
  files.forEach(file => {
    formData.append('files', file);
  });

  const response = await fetch('/api/grades/assignments/submit', {
    method: 'POST',
    headers: {
      'X-User-Id': studentId,
      'X-User-Role': 'STUDENT',
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  return response.json();
};
```

### 👥 Group Management

```typescript
// Create student group
const createGroup = async (groupData: {
  name: string;
  description: string;
  courseSessionId: number;
  members: Array<{studentId: number; studentName: string; role: 'LEADER' | 'MEMBER'}>
}) => {
  const response = await fetch('/api/grades/groups', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': userId,
      'X-User-Role': userRole,
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      ...groupData,
      type: 'STUDENT_CREATED'
    })
  });
  return response.json();
};
```

## 🔧 Features

### Assignment System
- **File Attachments**: Support for multiple file types via Resource Service
- **Due Date Management**: Automatic late submission detection
- **Status Tracking**: Draft, Published, Closed states
- **Group Assignments**: Support for collaborative work

### Grading System
- **Multiple Grade Types**: Exams, Quizzes, Assignments, Participation
- **Weighted Calculations**: Configurable weight percentages
- **Bulk Operations**: Import/export via Excel
- **Default Templates**: Pre-configured grade types

### Student Experience
- **Unified Assessment View**: All assessments and grades in one place
- **Progress Tracking**: Overall grade calculation and letter grades
- **Status Indicators**: Clear visual feedback on submission status
- **File Management**: Easy access to assignment files and submissions

### Group Collaboration
- **Flexible Groups**: Student or lecturer-created groups
- **Role Management**: Leaders and members with different permissions
- **Group Grading**: Assign same grade to all group members
- **Multi-Assignment**: Groups can work on multiple assignments

## 🔍 Error Handling

### Common HTTP Status Codes

- **200 OK**: Successful operation
- **201 Created**: Resource created successfully
- **204 No Content**: Successful deletion
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate submission or constraint violation
- **413 Payload Too Large**: File size exceeded
- **500 Internal Server Error**: System error

### Error Response Format

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Assignment deadline has passed",
  "timestamp": "2024-03-15T10:30:00Z",
  "path": "/api/grades/assignments/submit"
}
```

## 🧪 Testing Examples

### Assignment Workflow

```bash
# Create assignment (Lecturer)
curl -X POST http://localhost:8760/api/grades/assignments \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  -F "title=Programming Assignment 2" \
  -F "description=Implement a binary search tree" \
  -F "courseSessionId=1" \
  -F "dueDate=2024-04-15T23:59:59" \
  -F "maxScore=100" \
  -F "type=INDIVIDUAL" \
  -F "files=@instructions.pdf"

# Publish assignment
curl -X POST http://localhost:8760/api/grades/assignments/1/publish \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER"

# Submit assignment (Student)
curl -X POST http://localhost:8760/api/grades/assignments/submit \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  -F "assignmentId=1" \
  -F "content=My solution explanation" \
  -F "files=@solution.py"

# Grade submission (Lecturer)
curl -X POST http://localhost:8760/api/grades/assignments/submissions/1/grade \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "score": 85.0,
    "feedback": "Good implementation! Consider adding error handling."
  }'
```

### Grading Workflow

```bash
# Create default grade types
curl -X POST http://localhost:8760/api/grades/grading/grade-types/course-session/1/defaults \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER"

# Add student grade
curl -X POST http://localhost:8760/api/grades/grading/grades \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "studentId": 456,
    "gradeTypeId": 1,
    "score": 87.0,
    "feedback": "Good work on the midterm!"
  }'

# Get gradebook
curl -X GET http://localhost:8760/api/grades/grading/gradebook/course-session/1 \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER"

# Export gradebook
curl -X GET http://localhost:8760/api/grades/grading/gradebook/course-session/1/export \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  --output gradebook.xlsx
```

### Student Assessment View

```bash
# Get student assessment overview
curl -X GET http://localhost:8760/api/grades/grading/my-assessments/course-session/1 \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT"

# Get student assignments
curl -X GET http://localhost:8760/api/grades/assignments/student \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT"
```

### Group Management

```bash
# Create group (Student)
curl -X POST http://localhost:8760/api/grades/groups \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "name": "Team Alpha",
    "description": "Web development project group",
    "courseSessionId": 1,
    "type": "STUDENT_CREATED",
    "members": [
      {
        "studentId": 456,
        "studentName": "John Doe",
        "role": "LEADER"
      }
    ]
  }'

# Add member to group
curl -X POST http://localhost:8760/api/grades/groups/1/members \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  -d '{
    "studentId": 457,
    "studentName": "Jane Smith",
    "role": "MEMBER"
  }'
```

## 📊 Database Schema

### Core Tables

- **assignments**: Assignment definitions and metadata
- **submissions**: Student assignment submissions
- **grade_types**: Grade categories and weights
- **student_grades**: Individual student grades
- **student_groups**: Group definitions
- **group_members**: Group membership and roles

### Key Relationships

- Assignment → Submission (One-to-Many)
- GradeType → StudentGrade (One-to-Many)
- StudentGroup → GroupMember (One-to-Many)
- Assignment → GradeType (Optional One-to-One for assignment-based grades)

## 🔐 Security Features

- **Role-based Access Control**: Students, Lecturers have different permissions
- **Ownership Validation**: Users can only modify their own content
- **Course Session Isolation**: Data is isolated by course session
- **File Upload Security**: Integration with Resource Service for secure file handling
- **Grade Integrity**: Only lecturers can assign grades

## 📈 Performance Considerations

- **Database Indexing**: Optimized queries on student ID, course session ID
- **File Handling**: Efficient integration with Resource Service
- **Bulk Operations**: Excel import/export for large datasets
- **Caching**: Gradebook calculations cached for performance
- **Pagination**: Large result sets are paginated

## 🔄 Integration Points

### Resource Service
- File upload and download for assignments and submissions
- Secure file storage and access control

### Course Service
- Course session validation
- Student enrollment verification
- Lecturer assignment validation

### User Service
- User information retrieval
- Name and email resolution

## 🚨 Troubleshooting

### Common Issues

1. **Assignment Submission Fails**
   - Check file size limits (50MB default)
   - Verify assignment is published
   - Ensure deadline hasn't passed

2. **Grade Calculation Issues**
   - Verify grade type weights sum to 100%
   - Check for missing grades in calculation
   - Ensure grade types are active

3. **Group Access Denied**
   - Verify user is group member or creator
   - Check group is active
   - Validate course session access

### Debug Logging

```yaml
logging:
  level:
    com.cams.grade_service: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

## 🎯 Frontend Development Tips

### State Management
- Cache gradebook data to avoid repeated API calls
- Implement optimistic updates for grade changes
- Use loading states for file uploads

### User Experience
- Show progress indicators for file uploads
- Implement auto-save for draft submissions
- Provide clear feedback for grade updates

### Performance
- Lazy load assignment attachments
- Implement virtual scrolling for large gradebooks
- Use debounced search for student filtering

### Error Handling
- Graceful degradation for network issues
- Clear error messages for validation failures
- Retry mechanisms for failed uploads

This comprehensive Grade Service provides a robust foundation for academic assessment management, supporting both individual and collaborative learning environments with sophisticated grading capabilities and excellent user experience for both students and lecturers.