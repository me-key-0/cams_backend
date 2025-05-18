package com.cams.resource_service.model;

import com.cams.resource_service.model.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a category for organizing resources.
 * Categories can be predefined or user-created.
 */
@Entity
@Table(name = "resource_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isPredefined;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType allowedType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPredefined == null) {
            isPredefined = false;
        }
    }
} 