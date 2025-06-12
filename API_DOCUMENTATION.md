# CAMS WebSocket Chat API Documentation

## 🔌 WebSocket Connection

### Connection URL
```
ws://localhost:8765/ws/chat?userId=123&userName=John&userRole=STUDENT&roomId=1_123_456
```

### Query Parameters
- `userId` (required): User ID
- `userName` (required): Display name
- `userRole` (required): STUDENT or LECTURER
- `roomId` (optional): Auto-join specific room

---

## 📡 REST API Endpoints

### Base URL: `http://localhost:8760`

---

## 🎓 Chat Room Management

### 1. Create/Get Chat Room
**POST** `/api/com/chat/rooms`

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

**Response:**
```json
{
  "id": 1,
  "roomId": "1_123_456",
  "courseSessionId": 1,
  "studentId": 123,
  "studentName": "John Doe",
  "lecturerId": 456,
  "lecturerName": "Dr. Jane Smith",
  "createdAt": "2024-01-15T10:30:00",
  "lastActivity": "2024-01-15T14:25:00",
  "isActive": true,
  "unreadCount": 3,
  "lastMessage": {
    "id": 15,
    "content": "Thanks for the clarification!",
    "senderName": "John Doe",
    "sentAt": "2024-01-15T14:25:00"
  },
  "participants": [
    {
      "userId": 123,
      "userName": "John Doe",
      "userRole": "STUDENT",
      "isOnline": true
    },
    {
      "userId": 456,
      "userName": "Dr. Jane Smith",
      "userRole": "LECTURER",
      "isOnline": false,
      "lastSeenAt": "2024-01-15T13:45:00"
    }
  ]
}
```

### 2. Get Specific Chat Room
**GET** `/api/com/chat/rooms/{roomId}`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
```

### 3. Get User's Chat Rooms
**GET** `/api/com/chat/rooms`

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
```

### 4. Get Lecturer's Course Chat Rooms
**GET** `/api/com/chat/rooms/course/{courseSessionId}`

**Path Variables:**
- `courseSessionId`: 1

**Headers:**
```
X-User-Id: 456
X-User-Role: LECTURER
```

---

## 💬 Message Management

### 5. Send Message (REST)
**POST** `/api/com/chat/messages`

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
Content-Type: application/json
```

**Body:**
```json
{
  "roomId": "1_123_456",
  "content": "Hello! I have a question about the assignment.",
  "messageType": "TEXT"
}
```

### 6. Get Chat History (Paginated)
**GET** `/api/com/chat/rooms/{roomId}/messages`

**Path Variables:**
- `roomId`: 1_123_456

**Query Parameters:**
- `page`: 0
- `size`: 20

**Headers:**
```
X-User-Id: 123
```

### 7. Get Recent Messages
**GET** `/api/com/chat/rooms/{roomId}/messages/recent`

**Path Variables:**
- `roomId`: 1_123_456

**Query Parameters:**
- `limit`: 50

**Headers:**
```
X-User-Id: 123
```

---

## 📊 Message Status

### 8. Mark Messages as Read
**POST** `/api/com/chat/rooms/{roomId}/mark-read`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
```

### 9. Get Unread Message Count
**GET** `/api/com/chat/rooms/{roomId}/unread-count`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
```

**Response:**
```json
5
```

---

## 👥 Participant Management

### 10. Get Chat Room Participants
**GET** `/api/com/chat/rooms/{roomId}/participants`

**Path Variables:**
- `roomId`: 1_123_456

### 11. Join Chat Room
**POST** `/api/com/chat/rooms/{roomId}/join`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
```

### 12. Leave Chat Room
**POST** `/api/com/chat/rooms/{roomId}/leave`

**Path Variables:**
- `roomId`: 1_123_456

**Headers:**
```
X-User-Id: 123
```

### 13. Send Typing Indicator
**POST** `/api/com/chat/rooms/{roomId}/typing`

**Path Variables:**
- `roomId`: 1_123_456

**Query Parameters:**
- `isTyping`: true

**Headers:**
```
X-User-Id: 123
X-User-Role: STUDENT
```

---

## 🎯 WebSocket Message Types

### Send Message
```json
{
  "type": "SEND_MESSAGE",
  "roomId": "1_123_456",
  "content": "Hello, how are you?",
  "messageType": "TEXT"
}
```

### Join Room
```json
{
  "type": "JOIN_ROOM",
  "roomId": "1_123_456"
}
```

### Leave Room
```json
{
  "type": "LEAVE_ROOM",
  "roomId": "1_123_456"
}
```

### Typing Indicator
```json
{
  "type": "TYPING",
  "roomId": "1_123_456",
  "isTyping": true
}
```

### Mark Messages as Read
```json
{
  "type": "MARK_READ",
  "roomId": "1_123_456"
}
```

---

## 📋 Postman Collection JSON

```json
{
  "info": {
    "name": "CAMS Chat API",
    "description": "WebSocket Chat API for CAMS Communication Service",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8760",
      "type": "string"
    },
    {
      "key": "wsUrl",
      "value": "ws://localhost:8765",
      "type": "string"
    }
  ],
  "item": [
    {
      "name": "Chat Room Management",
      "item": [
        {
          "name": "Create Chat Room",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              },
              {
                "key": "X-User-Role",
                "value": "STUDENT"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms?courseSessionId=1&studentId=123&studentName=John Doe&lecturerId=456&lecturerName=Dr. Jane Smith",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms"],
              "query": [
                {
                  "key": "courseSessionId",
                  "value": "1"
                },
                {
                  "key": "studentId",
                  "value": "123"
                },
                {
                  "key": "studentName",
                  "value": "John Doe"
                },
                {
                  "key": "lecturerId",
                  "value": "456"
                },
                {
                  "key": "lecturerName",
                  "value": "Dr. Jane Smith"
                }
              ]
            }
          }
        },
        {
          "name": "Get Chat Room",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456"]
            }
          }
        },
        {
          "name": "Get User Chat Rooms",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              },
              {
                "key": "X-User-Role",
                "value": "STUDENT"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms"]
            }
          }
        },
        {
          "name": "Get Lecturer Course Rooms",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-User-Id",
                "value": "456"
              },
              {
                "key": "X-User-Role",
                "value": "LECTURER"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/course/1",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "course", "1"]
            }
          }
        }
      ]
    },
    {
      "name": "Message Management",
      "item": [
        {
          "name": "Send Message",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              },
              {
                "key": "X-User-Role",
                "value": "STUDENT"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"roomId\": \"1_123_456\",\n  \"content\": \"Hello! I have a question about the assignment.\",\n  \"messageType\": \"TEXT\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/messages",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "messages"]
            }
          }
        },
        {
          "name": "Get Chat History",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/messages?page=0&size=20",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "messages"],
              "query": [
                {
                  "key": "page",
                  "value": "0"
                },
                {
                  "key": "size",
                  "value": "20"
                }
              ]
            }
          }
        },
        {
          "name": "Get Recent Messages",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/messages/recent?limit=50",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "messages", "recent"],
              "query": [
                {
                  "key": "limit",
                  "value": "50"
                }
              ]
            }
          }
        }
      ]
    },
    {
      "name": "Message Status",
      "item": [
        {
          "name": "Mark Messages as Read",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/mark-read",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "mark-read"]
            }
          }
        },
        {
          "name": "Get Unread Count",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/unread-count",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "unread-count"]
            }
          }
        }
      ]
    },
    {
      "name": "Participant Management",
      "item": [
        {
          "name": "Get Participants",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/participants",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "participants"]
            }
          }
        },
        {
          "name": "Join Room",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              },
              {
                "key": "X-User-Role",
                "value": "STUDENT"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/join",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "join"]
            }
          }
        },
        {
          "name": "Leave Room",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/leave",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "leave"]
            }
          }
        },
        {
          "name": "Send Typing Indicator",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "X-User-Id",
                "value": "123"
              },
              {
                "key": "X-User-Role",
                "value": "STUDENT"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/com/chat/rooms/1_123_456/typing?isTyping=true",
              "host": ["{{baseUrl}}"],
              "path": ["api", "com", "chat", "rooms", "1_123_456", "typing"],
              "query": [
                {
                  "key": "isTyping",
                  "value": "true"
                }
              ]
            }
          }
        }
      ]
    }
  ]
}
```

---

## 🧪 Testing Workflow

### 1. Student Workflow
```bash
# 1. Create/Get chat room with lecturer
POST /api/com/chat/rooms?courseSessionId=1&studentId=123&studentName=John&lecturerId=456&lecturerName=Dr.Smith
Headers: X-User-Id: 123, X-User-Role: STUDENT

# 2. Get student's chat rooms
GET /api/com/chat/rooms
Headers: X-User-Id: 123, X-User-Role: STUDENT

# 3. Send message
POST /api/com/chat/messages
Headers: X-User-Id: 123, X-User-Role: STUDENT
Body: {"roomId": "1_123_456", "content": "Hello!", "messageType": "TEXT"}

# 4. Get chat history
GET /api/com/chat/rooms/1_123_456/messages?page=0&size=20
Headers: X-User-Id: 123

# 5. Mark messages as read
POST /api/com/chat/rooms/1_123_456/mark-read
Headers: X-User-Id: 123
```

### 2. Lecturer Workflow
```bash
# 1. Get lecturer's chat rooms for specific course
GET /api/com/chat/rooms/course/1
Headers: X-User-Id: 456, X-User-Role: LECTURER

# 2. Get all lecturer's chat rooms
GET /api/com/chat/rooms
Headers: X-User-Id: 456, X-User-Role: LECTURER

# 3. Send message to student
POST /api/com/chat/messages
Headers: X-User-Id: 456, X-User-Role: LECTURER
Body: {"roomId": "1_123_456", "content": "Hi! How can I help?", "messageType": "TEXT"}

# 4. Get unread message count
GET /api/com/chat/rooms/1_123_456/unread-count
Headers: X-User-Id: 456
```

---

## 🔧 WebSocket Testing

### Using WebSocket Client (Browser Console)
```javascript
// Connect to WebSocket
const ws = new WebSocket('ws://localhost:8765/ws/chat?userId=123&userName=John&userRole=STUDENT&roomId=1_123_456');

// Send message
ws.send(JSON.stringify({
  type: "SEND_MESSAGE",
  roomId: "1_123_456",
  content: "Hello from WebSocket!",
  messageType: "TEXT"
}));

// Join room
ws.send(JSON.stringify({
  type: "JOIN_ROOM",
  roomId: "1_123_456"
}));

// Send typing indicator
ws.send(JSON.stringify({
  type: "TYPING",
  roomId: "1_123_456",
  isTyping: true
}));

// Mark messages as read
ws.send(JSON.stringify({
  type: "MARK_READ",
  roomId: "1_123_456"
}));
```

---

## 📝 Environment Variables

### Postman Environment
```json
{
  "name": "CAMS Chat Environment",
  "values": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8760",
      "enabled": true
    },
    {
      "key": "wsUrl",
      "value": "ws://localhost:8765",
      "enabled": true
    },
    {
      "key": "studentId",
      "value": "123",
      "enabled": true
    },
    {
      "key": "lecturerId",
      "value": "456",
      "enabled": true
    },
    {
      "key": "courseSessionId",
      "value": "1",
      "enabled": true
    },
    {
      "key": "roomId",
      "value": "1_123_456",
      "enabled": true
    }
  ]
}
```