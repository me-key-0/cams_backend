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

/**
 * Entity representing a resource material in the system.
 * This can be a document, video, photo, link, or folder.
 */
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
    private String fileUrl;

    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    private Long fileSize; // in bytes

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
    
    private String originalFileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        downloadCount = 0;
        if (status == null) {
            status = ResourceStatus.ACTIVE;
        }
    }
} 