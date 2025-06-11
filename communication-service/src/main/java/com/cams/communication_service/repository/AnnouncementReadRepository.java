package com.cams.communication_service.repository;

import com.cams.communication_service.model.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, Long> {
    
    // Check if a user has read a specific announcement
    Optional<AnnouncementRead> findByAnnouncementIdAndUserIdAndUserRole(Long announcementId, String userId, String userRole);
    
    // Get all read announcements for a user
    List<AnnouncementRead> findByUserIdAndUserRole(String userId, String userRole);
    
    // Count unread announcements for a user
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.active = true AND " +
           "(a.isGlobal = true OR a.departmentCode = :departmentCode) AND " +
           "a.id NOT IN (SELECT ar.announcement.id FROM AnnouncementRead ar WHERE ar.userId = :userId AND ar.userRole = :userRole)")
    long countUnreadAnnouncementsForUser(@Param("userId") String userId, 
                                        @Param("userRole") String userRole,
                                        @Param("departmentCode") String departmentCode);
    
    // Count unread announcements for a user by category
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.active = true AND a.category = :category AND " +
           "(a.isGlobal = true OR a.departmentCode = :departmentCode) AND " +
           "a.id NOT IN (SELECT ar.announcement.id FROM AnnouncementRead ar WHERE ar.userId = :userId AND ar.userRole = :userRole)")
    long countUnreadAnnouncementsForUserByCategory(@Param("userId") String userId, 
                                                  @Param("userRole") String userRole,
                                                  @Param("departmentCode") String departmentCode,
                                                  @Param("category") Announcement.Category category);
}