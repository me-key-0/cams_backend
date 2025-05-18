package com.cams.resource_service.repository;

import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ResourceMaterial entity.
 * Provides methods for accessing and managing resource materials.
 */
@Repository
public interface ResourceMaterialRepository extends JpaRepository<ResourceMaterial, Long> {
    
    /**
     * Find all active resources for a specific course session
     */
    List<ResourceMaterial> findByCourseSessionIdAndStatus(Long courseSessionId, ResourceStatus status);

    /**
     * Find resources by type and status for a course session
     */
    List<ResourceMaterial> findByCourseSessionIdAndTypeAndStatus(Long courseSessionId, ResourceType type, ResourceStatus status);

    /**
     * Find resources by category for a course session
     */
    @Query("SELECT r FROM ResourceMaterial r JOIN r.categories c WHERE r.courseSessionId = ?1 AND c = ?2 AND r.status = ?3")
    List<ResourceMaterial> findByCourseSessionIdAndCategory(Long courseSessionId, String category, ResourceStatus status);

    /**
     * Find active resource by ID and course session ID
     */
    Optional<ResourceMaterial> findByIdAndCourseSessionIdAndStatus(Long id, Long courseSessionId, ResourceStatus status);

    /**
     * Find all resources uploaded by a specific user
     */
    List<ResourceMaterial> findByUploadedByAndStatus(Long uploadedBy, ResourceStatus status);

    /**
     * Search resources by title or description in a course session
     */
    @Query("SELECT r FROM ResourceMaterial r WHERE r.courseSessionId = ?1 AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', ?2, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', ?2, '%'))) AND " +
           "r.status = ?3")
    List<ResourceMaterial> searchInCourseSession(Long courseSessionId, String searchTerm, ResourceStatus status);
} 