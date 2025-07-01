# Communication Service

The Communication Service manages announcements, notifications, and support tickets for the CAMS system.

## 🎯 Purpose

- System-wide announcements management
- Course-specific notifications
- Student-admin communication via tickets
- Read status tracking
- Category-based filtering

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- Course Service running (for course session validation)

### Configuration

```yaml
server:
  port: 8765

spring:
  application:
    name: communication-service
  datasource:
    url: jdbc:mariadb://localhost:3306/communication_db
    username: root
    password: $904380
```

### Running the Service

```bash
cd communication-service
mvn spring-boot:run
```

## 📡 API Endpoints

## 📢 Announcements

### Create Announcement
**POST** `/api/com/announcements`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `X-User-Id`: Admin ID
- `X-User-Department`: Department code

**Request Body:**
```json
{
  "title": "Important Academic Update",
  "content": "Please note the changes in the academic calendar...",
  "category": "ACADEMIC"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Important Academic Update",
  "content": "Please note the changes in the academic calendar...",
  "category": "ACADEMIC",
  "createdAt": "2024-01-15T10:30:00",
  "createdBy": "123",
  "createdByName": "Admin John",
  "role": "ADMIN",
  "departmentCode": "CS",
  "isGlobal": false,
  "active": true,
  "isRead": false
}
```

### Get Announcements for User
**GET** `/api/com/announcements`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID
- `X-User-Department`: Department code

**Response:**
```json
{
  "totalAnnouncements": 5,
  "unreadCount": 2,
  "announcements": [
    {
      "id": 1,
      "title": "Important Academic Update",
      "content": "Please note the changes...",
      "category": "ACADEMIC",
      "createdAt": "2024-01-15T10:30:00",
      "createdBy": "123",
      "createdByName": "Admin John",
      "role": "ADMIN",
      "departmentCode": "CS",
      "isGlobal": false,
      "active": true,
      "isRead": false
    }
  ]
}
```

### Get Announcements by Category
**GET** `/api/com/announcements/category/{category}`

**Path Parameters:**
- `category`: ACADEMIC, ADMINISTRATIVE

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID
- `X-User-Department`: Department code

**Response:**
```json
{
  "totalAnnouncements": 3,
  "unreadCount": 1,
  "announcements": [...]
}
```

### Get Unread Count
**GET** `/api/com/announcements/unread-count`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID
- `X-User-Department`: Department code

**Response:**
```json
2
```

### Mark as Read
**POST** `/api/com/announcements/{id}/mark-read`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Id`: User ID

**Response:**
```json
200 OK
```

### Get All Announcements (Admin)
**GET** `/api/com/announcements/admin`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "title": "Important Academic Update",
    "content": "Please note the changes...",
    "category": "ACADEMIC",
    "createdAt": "2024-01-15T10:30:00",
    "createdBy": "123",
    "createdByName": "Admin John",
    "role": "ADMIN",
    "departmentCode": "CS",
    "isGlobal": false,
    "active": true
  }
]
```

### Get My Announcements (Admin)
**GET** `/api/com/announcements/my-announcements`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `X-User-Id`: Admin ID

**Response:**
```json
[
  {
    "id": 1,
    "title": "My Announcement",
    "createdBy": "123"
  }
]
```

### Update Announcement
**PUT** `/api/com/announcements/{id}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `X-User-Id`: Admin ID (must be creator)
- `X-User-Department`: Department code

**Request Body:**
```json
{
  "title": "Updated Title",
  "content": "Updated content",
  "category": "ADMINISTRATIVE"
}
```

### Delete Announcement
**DELETE** `/api/com/announcements/{id}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN
- `X-User-Id`: Admin ID (must be creator)
- `X-User-Department`: Department code

**Response:**
```json
204 No Content
```

### Get Specific Announcement
**GET** `/api/com/announcements/{id}`

**Headers:**
- `X-User-Role`: Any role
- `X-User-Id`: User ID

**Response:**
```json
{
  "id": 1,
  "title": "Important Academic Update",
  "content": "Please note the changes...",
  "category": "ACADEMIC",
  "createdAt": "2024-01-15T10:30:00",
  "isRead": true
}
```

## 🔔 Notifications

### Create Notification (Lecturer)
**POST** `/api/com/notifications`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Request Body:**
```json
{
  "subject": "Assignment Deadline Reminder",
  "message": "Don't forget to submit your assignment by Friday",
  "type": "deadline",
  "courseSessionId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "subject": "Assignment Deadline Reminder",
  "message": "Don't forget to submit your assignment by Friday",
  "type": "deadline",
  "courseSessionId": 1,
  "lecturerId": "123",
  "lecturerName": "Dr. Jane Smith",
  "createdAt": "2024-01-15T10:30:00",
  "active": true,
  "isRead": false
}
```

### Get My Notifications (Lecturer)
**GET** `/api/com/notifications/my-notifications`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Response:**
```json
[
  {
    "id": 1,
    "subject": "Assignment Deadline Reminder",
    "lecturerId": "123",
    "lecturerName": "Dr. Jane Smith"
  }
]
```

### Get Notifications for Student
**GET** `/api/com/notifications/student`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
{
  "totalNotifications": 8,
  "unreadCount": 3,
  "notifications": [
    {
      "id": 1,
      "subject": "Assignment Deadline Reminder",
      "message": "Don't forget to submit...",
      "type": "deadline",
      "courseSessionId": 1,
      "lecturerId": "123",
      "lecturerName": "Dr. Jane Smith",
      "createdAt": "2024-01-15T10:30:00",
      "active": true,
      "isRead": false
    }
  ]
}
```

### Get Unread Notification Count (Student)
**GET** `/api/com/notifications/student/unread-count`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
3
```

### Get Notifications by Course Session
**GET** `/api/com/notifications/course-session/{courseSessionId}`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
[
  {
    "id": 1,
    "subject": "Course Update",
    "courseSessionId": 1,
    "isRead": true
  }
]
```

### Get Notifications by Type
**GET** `/api/com/notifications/course-session/{courseSessionId}/type/{type}`

**Path Parameters:**
- `type`: deadline, reminder, announcement, etc.

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
[
  {
    "id": 1,
    "subject": "Assignment Deadline",
    "type": "deadline",
    "isRead": false
  }
]
```

### Mark Notification as Read
**POST** `/api/com/notifications/{notificationId}/mark-read`

**Headers:**
- `X-User-Id`: Student ID
- `X-User-Role`: STUDENT

**Response:**
```json
200 OK
```

### Delete Notification (Lecturer)
**DELETE** `/api/com/notifications/{notificationId}`

**Headers:**
- `X-User-Id`: Lecturer ID (must be creator)
- `X-User-Role`: LECTURER

**Response:**
```json
204 No Content
```

### Get Specific Notification
**GET** `/api/com/notifications/{notificationId}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
{
  "id": 1,
  "subject": "Assignment Deadline Reminder",
  "message": "Don't forget to submit...",
  "type": "deadline",
  "courseSessionId": 1,
  "isRead": true
}
```

## 🎫 Support Tickets

### Create Ticket
**POST** `/api/com/tickets`

**Headers:**
- `X-User-Id`: Student/Lecturer ID
- `X-User-Role`: STUDENT, LECTURER
- `X-User-Department`: Department code

**Request Body:**
```json
{
  "subject": "Grade Inquiry",
  "message": "I have a question about my midterm grade...",
  "priority": "MEDIUM"
}
```

**Response:**
```json
{
  "id": 1,
  "subject": "Grade Inquiry",
  "message": "I have a question about my midterm grade...",
  "priority": "MEDIUM",
  "status": "OPEN",
  "senderRole": "STUDENT",
  "senderId": "456",
  "senderName": "John Doe",
  "departmentCode": "CS",
  "createdAt": "2024-01-15T10:30:00",
  "isRead": false,
  "responses": []
}
```

### Get My Tickets
**GET** `/api/com/tickets/my-tickets`

**Headers:**
- `X-User-Id`: User ID

**Response:**
```json
[
  {
    "id": 1,
    "subject": "Grade Inquiry",
    "priority": "MEDIUM",
    "status": "OPEN",
    "createdAt": "2024-01-15T10:30:00",
    "isRead": false
  }
]
```

### Get All Tickets (Admin)
**GET** `/api/com/tickets/admin`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "subject": "Grade Inquiry",
    "priority": "MEDIUM",
    "status": "OPEN",
    "senderRole": "STUDENT",
    "senderId": "456",
    "senderName": "John Doe",
    "departmentCode": "CS",
    "createdAt": "2024-01-15T10:30:00",
    "isRead": false,
    "responses": []
  }
]
```

### Get Filtered Tickets (Admin)
**GET** `/api/com/tickets/admin/filtered`

**Query Parameters:**
- `priority`: LOW, MEDIUM, HIGH
- `senderRole`: STUDENT, LECTURER
- `status`: OPEN, IN_PROGRESS, RESOLVED
- `isRead`: true, false

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "subject": "High Priority Issue",
    "priority": "HIGH",
    "status": "OPEN",
    "isRead": false
  }
]
```

### Respond to Ticket (Admin)
**POST** `/api/com/tickets/{ticketId}/respond`

**Headers:**
- `X-User-Id`: Admin ID
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Request Body:**
```json
{
  "message": "Thank you for your inquiry. I'll look into this matter...",
  "newStatus": "IN_PROGRESS"
}
```

**Response:**
```json
{
  "id": 1,
  "subject": "Grade Inquiry",
  "status": "IN_PROGRESS",
  "responses": [
    {
      "id": 1,
      "message": "Thank you for your inquiry...",
      "responderId": "789",
      "responderName": "Admin Smith",
      "createdAt": "2024-01-15T11:00:00"
    }
  ]
}
```

### Mark Ticket as Read (Admin)
**PUT** `/api/com/tickets/{ticketId}/mark-read`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
200 OK
```

### Update Ticket Status (Admin)
**PUT** `/api/com/tickets/{ticketId}/status`

**Query Parameters:**
- `status`: OPEN, IN_PROGRESS, RESOLVED

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
{
  "id": 1,
  "subject": "Grade Inquiry",
  "status": "RESOLVED"
}
```

### Delete Ticket (Admin)
**DELETE** `/api/com/tickets/{ticketId}`

**Headers:**
- `X-User-Role`: ADMIN, SUPER_ADMIN

**Response:**
```json
204 No Content
```

### Get Specific Ticket
**GET** `/api/com/tickets/{ticketId}`

**Headers:**
- `X-User-Role`: Any role
- `X-User-Id`: User ID

**Response:**
```json
{
  "id": 1,
  "subject": "Grade Inquiry",
  "message": "I have a question about my midterm grade...",
  "priority": "MEDIUM",
  "status": "IN_PROGRESS",
  "senderRole": "STUDENT",
  "senderId": "456",
  "senderName": "John Doe",
  "departmentCode": "CS",
  "createdAt": "2024-01-15T10:30:00",
  "isRead": true,
  "responses": [
    {
      "id": 1,
      "message": "Thank you for your inquiry...",
      "responderId": "789",
      "responderName": "Admin Smith",
      "createdAt": "2024-01-15T11:00:00"
    }
  ]
}
```

## 🏗️ Data Models

### Announcement Entity

```java
@Entity
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private Category category;        // ACADEMIC, ADMINISTRATIVE
    private LocalDateTime createdAt;
    private String createdBy;         // Admin ID
    private String createdByName;     // Admin name
    private String role;              // ADMIN, SUPER_ADMIN
    private String departmentCode;    // For department-specific
    private boolean active;
    private boolean isGlobal;         // SUPER_ADMIN announcements
    private List<AnnouncementRead> readStatuses;
}
```

### Notification Entity

```java
@Entity
public class Notification {
    private Long id;
    private String subject;
    private String message;
    private String type;              // deadline, reminder, custom
    private Long courseSessionId;
    private String lecturerId;
    private String lecturerName;
    private LocalDateTime createdAt;
    private boolean active;
    private List<NotificationRead> readStatuses;
}
```

### Ticket Entity

```java
@Entity
public class Ticket {
    private Long id;
    private String subject;
    private String message;
    private Priority priority;        // LOW, MEDIUM, HIGH
    private Status status;           // OPEN, IN_PROGRESS, RESOLVED
    private String senderRole;       // STUDENT, LECTURER
    private String senderId;
    private String senderName;
    private String departmentCode;
    private LocalDateTime createdAt;
    private boolean isRead;
    private List<TicketResponse> responses;
}
```

## 🔧 Features

### Announcements

- Department-specific and global announcements
- Category-based filtering (Academic/Administrative)
- Read status tracking per user
- Admin/Super Admin creation and management
- Unread count tracking

### Notifications

- Course-specific notifications from lecturers
- Type-based categorization
- Student read status tracking
- Automatic course enrollment integration
- Unread count per student

### Support Tickets

- Student/Lecturer to Admin communication
- Priority levels (Low/Medium/High)
- Status tracking (Open/In Progress/Resolved)
- Admin response system
- Department-based organization

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: System error

## 🧪 Testing

### Manual Testing Examples

```bash
# Create announcement (Admin)
curl -X POST http://localhost:8760/api/com/announcements \
  -H "Content-Type: application/json" \
  -H "X-User-Role: ADMIN" \
  -H "X-User-Id: 123" \
  -H "X-User-Department: CS" \
  -d '{
    "title": "Important Update",
    "content": "Please note the changes...",
    "category": "ACADEMIC"
  }'

# Get announcements (Student)
curl -X GET http://localhost:8760/api/com/announcements \
  -H "X-User-Role: STUDENT" \
  -H "X-User-Id: 456" \
  -H "X-User-Department: CS"

# Create notification (Lecturer)
curl -X POST http://localhost:8760/api/com/notifications \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 789" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "subject": "Assignment Reminder",
    "message": "Submit by Friday",
    "type": "deadline",
    "courseSessionId": 1
  }'

# Get notifications (Student)
curl -X GET http://localhost:8760/api/com/notifications/student \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT"

# Create ticket (Student)
curl -X POST http://localhost:8760/api/com/tickets \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  -H "X-User-Department: CS" \
  -d '{
    "subject": "Grade Inquiry",
    "message": "Question about my grade",
    "priority": "MEDIUM"
  }'

# Respond to ticket (Admin)
curl -X POST http://localhost:8760/api/com/tickets/1/respond \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "message": "I will look into this",
    "newStatus": "IN_PROGRESS"
  }'
```

## 📊 Database Schema

### Core Tables

- `announcements`: System announcements
- `announcement_reads`: Read status tracking
- `notifications`: Course notifications
- `notification_reads`: Read status tracking
- `tickets`: Support tickets
- `ticket_responses`: Admin responses

### Read Status Tracking

Both announcements and notifications track read status per user:
- User ID and role combination
- Read timestamp
- Automatic unread count calculation

## 🔐 Security Features

- Role-based access control
- Department-level data isolation
- Creator-only modification rights
- Secure user identification
- Input validation and sanitization

## 📝 Integration Points

### Course Service Integration

- Course session validation
- Student enrollment verification
- Lecturer assignment validation

### Used By

- **Frontend Applications**: Real-time notifications
- **Mobile Apps**: Push notification triggers

## 🚨 Troubleshooting

### Common Issues

1. **Announcement Not Visible**
   - Check user department
   - Verify announcement is active
   - Check global vs department-specific

2. **Notification Not Received**
   - Verify student enrollment in course
   - Check course session validity
   - Ensure notification is active

3. **Ticket Access Denied**
   - Verify user role permissions
   - Check ticket ownership
   - Validate department access

### Debug Logging

```yaml
logging:
  level:
    com.cams.communication_service: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

## 📈 Performance Considerations

- Database indexing on user and course session IDs
- Efficient unread count queries
- Optimized read status tracking
- Caching for frequently accessed data

## 🔄 Future Enhancements

- Real-time notifications via WebSocket
- Email notification integration
- Push notification support
- Advanced filtering and search
- Notification scheduling
- Bulk announcement management
- Rich text content support
- File attachments for tickets
- Automated ticket routing
- SLA tracking for ticket resolution