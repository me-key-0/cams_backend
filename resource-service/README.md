# Resource Service

The Resource Service manages file uploads, downloads, and resource sharing for the CAMS system.

## 🎯 Purpose

- File upload and storage management
- Resource categorization and organization
- Download tracking and analytics
- Link resource management
- Course-specific resource access control

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MariaDB 10.5+
- Course Service running (for course session validation)
- Local storage directory (configurable)

### Configuration

```yaml
server:
  port: 8767

spring:
  application:
    name: resource-service
  datasource:
    url: jdbc:mariadb://localhost:3306/resource_db
    username: root
    password: $904380
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

app:
  storage:
    base-directory: ./resources
    max-file-size: 52428800 # 50MB
```

### Running the Service

```bash
cd resource-service
mvn spring-boot:run
```

## 📡 API Endpoints

### File Resource Management

#### Upload File Resource
**POST** `/api/v1/resources`

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
**POST** `/api/v1/resources/link`

**Content-Type:** `application/json`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

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

**Response:**
```json
{
  "id": 2,
  "title": "External Tutorial",
  "description": "Helpful programming tutorial",
  "fileName": "link",
  "originalFileName": "External Link",
  "type": "LINK",
  "fileSize": 0,
  "mimeType": "text/html",
  "categories": ["tutorial", "external"],
  "downloadCount": 0,
  "uploadedAt": "2024-01-15T10:35:00",
  "courseSessionId": 1,
  "uploadedBy": 123,
  "uploaderName": "Dr. Jane Smith",
  "status": "ACTIVE",
  "linkUrl": "https://example.com/tutorial",
  "fileSizeFormatted": "0 B"
}
```

### Resource Retrieval

#### Get Resource by ID
**GET** `/api/v1/resources/{id}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER, ADMIN

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
  "downloadCount": 5,
  "uploadedAt": "2024-01-15T10:30:00",
  "courseSessionId": 1,
  "uploadedBy": 123,
  "uploaderName": "Dr. Jane Smith",
  "status": "ACTIVE",
  "downloadUrl": "/api/v1/resources/download/uuid-generated-filename.pdf/1",
  "fileSizeFormatted": "2.0 MB"
}
```

#### Get Resources by Course Session
**GET** `/api/v1/resources/course-session/{courseSessionId}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
[
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
    "downloadCount": 5,
    "uploadedAt": "2024-01-15T10:30:00",
    "courseSessionId": 1,
    "uploadedBy": 123,
    "uploaderName": "Dr. Jane Smith",
    "status": "ACTIVE",
    "downloadUrl": "/api/v1/resources/download/uuid-generated-filename.pdf/1",
    "fileSizeFormatted": "2.0 MB"
  }
]
```

#### Get Resources Statistics
**GET** `/api/v1/resources/course-session/{courseSessionId}/stats`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
{
  "totalResources": 15,
  "resourcesByType": {
    "Document": 8,
    "Video": 3,
    "Photo": 2,
    "Link": 2
  },
  "resourcesByCategory": {
    "lecture-notes": 5,
    "assignments": 3,
    "tutorials": 4,
    "references": 3
  },
  "resources": [...],
  "totalFileSize": 104857600,
  "totalFileSizeFormatted": "100.0 MB"
}
```

#### Get Resources by Type
**GET** `/api/v1/resources/course-session/{courseSessionId}/type/{type}`

**Path Parameters:**
- `type`: DOCUMENT, VIDEO, PHOTO, LINK, AUDIO, ARCHIVE

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
[
  {
    "id": 1,
    "title": "Lecture Video - Chapter 1",
    "type": "VIDEO",
    "fileSize": 52428800,
    "fileSizeFormatted": "50.0 MB"
  }
]
```

#### Get Resources by Category
**GET** `/api/v1/resources/course-session/{courseSessionId}/category/{category}`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
[
  {
    "id": 1,
    "title": "Assignment 1 Instructions",
    "categories": ["assignments"],
    "type": "DOCUMENT"
  }
]
```

#### Search Resources
**GET** `/api/v1/resources/course-session/{courseSessionId}/search?searchTerm={term}`

**Query Parameters:**
- `searchTerm`: Search term for title or description

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
[
  {
    "id": 1,
    "title": "Programming Fundamentals",
    "description": "Basic programming concepts and examples"
  }
]
```

#### Get My Resources (Lecturer)
**GET** `/api/v1/resources/my-resources`

**Headers:**
- `X-User-Id`: Lecturer ID
- `X-User-Role`: LECTURER

**Response:**
```json
[
  {
    "id": 1,
    "title": "My Uploaded Resource",
    "uploadedBy": 123,
    "uploaderName": "Dr. Jane Smith"
  }
]
```

### File Download

#### Download Resource
**GET** `/api/v1/resources/download/{fileName}/{id}`

**Headers:**
- `X-User-Id`: User ID
- `X-User-Role`: STUDENT, LECTURER

**Response:**
- File download with appropriate headers
- Content-Type based on file type
- Content-Disposition: attachment

**Example:**
```bash
curl -X GET "http://localhost:8760/api/v1/resources/download/uuid-filename.pdf/1" \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  --output downloaded-file.pdf
```

#### Record Download
**POST** `/api/v1/resources/{id}/download`

**Headers:**
- `X-User-Role`: STUDENT, LECTURER

**Response:**
```json
200 OK
```

### Resource Management

#### Update Resource
**PUT** `/api/v1/resources/{id}`

**Headers:**
- `X-User-Id`: Lecturer ID (must be owner)
- `X-User-Role`: LECTURER

**Request Body:**
```json
{
  "title": "Updated Resource Title",
  "description": "Updated description",
  "categories": ["updated-category", "new-category"],
  "linkUrl": "https://updated-link.com" // For LINK type only
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Updated Resource Title",
  "description": "Updated description",
  "categories": ["updated-category", "new-category"]
}
```

#### Delete Resource
**DELETE** `/api/v1/resources/{id}`

**Headers:**
- `X-User-Id`: Lecturer ID (must be owner)
- `X-User-Role`: LECTURER

**Response:**
```json
204 No Content
```

## 🏗️ Data Models

### Resource Material Entity

```java
@Entity
public class ResourceMaterial {
    private Long id;
    private String title;
    private String description;
    private String fileName;           // UUID-based filename
    private String originalFileName;   // Original user filename
    private String filePath;          // Full file path
    private ResourceType type;        // DOCUMENT, VIDEO, PHOTO, LINK, AUDIO, ARCHIVE
    private Long fileSize;           // Size in bytes
    private String mimeType;         // MIME type
    private Set<String> categories;  // Resource categories
    private Integer downloadCount;   // Download counter
    private LocalDateTime uploadedAt;
    private Long courseSessionId;
    private Long uploadedBy;         // Lecturer ID
    private String uploaderName;     // Lecturer name
    private ResourceStatus status;   // ACTIVE, ARCHIVED, DELETED
    private String linkUrl;          // For LINK type resources
}
```

### Resource Types

- **DOCUMENT**: PDF, Word, Excel, PowerPoint, Text files
- **VIDEO**: MP4, AVI, MOV, WMV, FLV, WebM, MKV
- **PHOTO**: JPG, PNG, GIF, BMP, SVG, WebP
- **LINK**: External URLs
- **AUDIO**: MP3, WAV, FLAC, AAC, OGG
- **ARCHIVE**: ZIP, RAR, 7Z, TAR, GZ

## 🔧 Features

### File Management

- Secure file upload with validation
- UUID-based filename generation
- File type validation
- Size limit enforcement (50MB default)
- Automatic directory creation

### Resource Organization

- Category-based organization
- Course session isolation
- Type-based filtering
- Search functionality
- Download tracking

### Access Control

- Lecturer upload permissions
- Course session-based access
- Owner-only modification
- Role-based download access

### Storage

- Local file system storage
- Configurable storage directory
- Automatic cleanup on deletion
- File existence validation

## 🔍 Error Handling

Common error responses:

- **400 Bad Request**: Invalid file type, size exceeded
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource/file not found
- **413 Payload Too Large**: File size exceeded
- **500 Internal Server Error**: Storage error

## 🧪 Testing

### Manual Testing Examples

```bash
# Upload a file resource
curl -X POST http://localhost:8760/api/v1/resources \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  -F "file=@/path/to/document.pdf" \
  -F "title=Lecture Notes" \
  -F "description=Chapter 1 notes" \
  -F "type=DOCUMENT" \
  -F "courseSessionId=1" \
  -F "categories=lecture-notes" \
  -F "categories=programming"

# Create a link resource
curl -X POST http://localhost:8760/api/v1/resources/link \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "title": "External Tutorial",
    "description": "Programming tutorial",
    "type": "LINK",
    "courseSessionId": 1,
    "categories": ["tutorial"],
    "linkUrl": "https://example.com/tutorial"
  }'

# Get course resources
curl -X GET http://localhost:8760/api/v1/resources/course-session/1 \
  -H "X-User-Role: STUDENT"

# Download a file
curl -X GET "http://localhost:8760/api/v1/resources/download/filename.pdf/1" \
  -H "X-User-Id: 456" \
  -H "X-User-Role: STUDENT" \
  --output downloaded-file.pdf

# Search resources
curl -X GET "http://localhost:8760/api/v1/resources/course-session/1/search?searchTerm=programming" \
  -H "X-User-Role: STUDENT"

# Update resource
curl -X PUT http://localhost:8760/api/v1/resources/1 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER" \
  -d '{
    "title": "Updated Title",
    "description": "Updated description",
    "categories": ["updated-category"]
  }'

# Delete resource
curl -X DELETE http://localhost:8760/api/v1/resources/1 \
  -H "X-User-Id: 123" \
  -H "X-User-Role: LECTURER"
```

## 📊 Database Schema

### Core Tables

- `resource_materials`: Main resource information
- `resource_material_categories`: Resource categories (many-to-many)

### Storage Structure

```
resources/
├── 1/                    # Course Session ID
│   ├── uuid-file1.pdf
│   ├── uuid-file2.docx
│   └── uuid-file3.mp4
├── 2/
│   └── uuid-file4.pptx
└── ...
```

## 🔐 Security Features

- File type validation
- Size limit enforcement
- Path traversal protection
- Access control validation
- Secure filename generation
- Course session isolation

## 📝 Integration Points

### Course Service Integration

- Course session validation
- Lecturer authorization
- Course information retrieval

### Used By

- **Communication Service**: Resource notifications
- **Grade Service**: Assignment resources

## 🚨 Troubleshooting

### Common Issues

1. **File Upload Fails**
   - Check file size limits
   - Verify file type is allowed
   - Ensure storage directory exists

2. **Download Not Working**
   - Verify file exists on disk
   - Check file permissions
   - Validate resource ID

3. **Access Denied**
   - Verify lecturer assignment to course
   - Check user role permissions
   - Validate course session access

### Debug Logging

```yaml
logging:
  level:
    com.cams.resource_service: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

## 📈 Performance Considerations

- File streaming for large downloads
- Database indexing on course session ID
- Efficient file existence checks
- Optimized search queries
- Caching for frequently accessed metadata

## 🔄 Future Enhancements

- Cloud storage integration (AWS S3, Google Cloud)
- File versioning system
- Bulk upload functionality
- Advanced search with filters
- File preview capabilities
- Compression for large files
- CDN integration for faster downloads
- Virus scanning integration