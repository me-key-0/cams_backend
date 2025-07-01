package com.cams.communication_service.repository;

import com.cams.communication_service.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    // Find all active announcements ordered by creation date
    List<Announcement> findByActiveTrueOrderByCreatedAtDesc();
    
    // Find announcements by department code
    List<Announcement> findByActiveTrueAndDepartmentCodeOrderByCreatedAtDesc(String departmentCode);
    
    // Find global announcements (SUPER_ADMIN announcements)
    List<Announcement> findByActiveTrueAndIsGlobalTrueOrderByCreatedAtDesc();
    
    // Find announcements by category
    List<Announcement> findByActiveTrueAndCategoryOrderByCreatedAtDesc(Announcement.Category category);
    
    // Find announcements by category and department
    List<Announcement> findByActiveTrueAndCategoryAndDepartmentCodeOrderByCreatedAtDesc(
        Announcement.Category category, String departmentCode);
    
    // Find global announcements by category
    List<Announcement> findByActiveTrueAndCategoryAndIsGlobalTrueOrderByCreatedAtDesc(
        Announcement.Category category);
    
    // Find announcements by creator
    List<Announcement> findByCreatedByAndActiveTrueOrderByCreatedAtDesc(String createdBy);
    
    // Custom query to get announcements for a user (global + department-specific)
    @Query("SELECT a FROM Announcement a WHERE a.active = true AND " +
           "(a.isGlobal = true OR a.departmentCode = :departmentCode) " +
           "ORDER BY a.createdAt DESC")
    List<Announcement> findAnnouncementsForUser(@Param("departmentCode") String departmentCode);
    
    // Custom query to get announcements for a user filtered by category
    @Query("SELECT a FROM Announcement a WHERE a.active = true AND a.category = :category AND " +
           "(a.isGlobal = true OR a.departmentCode = :departmentCode) " +
           "ORDER BY a.createdAt DESC")
    List<Announcement> findAnnouncementsForUserByCategory(@Param("departmentCode") String departmentCode, 
                                                         @Param("category") Announcement.Category category);
}