package com.cams.resource_service.model;

import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "resource_materials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String fileName; // Stored filename (UUID-based)

    @Column(nullable = false)
    private String originalFileName; // Original filename from user

    @Column(nullable = false)
    private String filePath; // Full path to file

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    private Long fileSize; // in bytes

    private String mimeType; // MIME type of the file

    @ElementCollection
    @CollectionTable(
        name = "resource_material_categories",
        joinColumns = @JoinColumn(name = "resource_id")
    )
    @Column(name = "category")
    private Set<String> categories;

    @Column(nullable = false)
    private Integer downloadCount;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private Long courseSessionId;

    @Column(nullable = false)
    private Long uploadedBy;

    @Column(nullable = false)
    private String uploaderName; // Name of the lecturer who uploaded

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

    // For LINK type resources
    private String linkUrl;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        downloadCount = 0;
        if (status == null) {
            status = ResourceStatus.ACTIVE;
        }
    }
}