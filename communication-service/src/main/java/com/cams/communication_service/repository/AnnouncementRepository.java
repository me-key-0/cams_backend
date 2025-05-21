package com.cams.communication_service.repository;

import com.cams.communication_service.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByActiveTrueOrderByCreatedAtDesc();
    
    List<Announcement> findByActiveTrueAndDepartmentCodeOrderByCreatedAtDesc(String departmentCode);
    
    List<Announcement> findByActiveTrueAndIsGlobalTrueOrderByCreatedAtDesc();
    
    List<Announcement> findByActiveTrueAndRoleInOrderByCreatedAtDesc(List<String> roles);
}
