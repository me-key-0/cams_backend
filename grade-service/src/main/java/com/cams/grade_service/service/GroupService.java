package com.cams.grade_service.service;

import com.cams.grade_service.dto.*;

import java.util.List;

public interface GroupService {
    
    // Group Management
    GroupResponse createGroup(GroupRequest request, Long createdBy, String createdByName);
    GroupResponse updateGroup(Long id, GroupRequest request, Long userId);
    void deleteGroup(Long id, Long userId);
    GroupResponse getGroupById(Long id);
    List<GroupResponse> getGroupsByCourseSession(Long courseSessionId);
    List<GroupResponse> getMyGroups(Long userId);
    List<GroupResponse> getStudentGroups(Long studentId, Long courseSessionId);
    
    // Group Member Management
    GroupMemberResponse addMemberToGroup(Long groupId, GroupMemberRequest request, Long userId);
    void removeMemberFromGroup(Long groupId, Long studentId, Long userId);
    List<GroupMemberResponse> getGroupMembers(Long groupId);
    GroupMemberResponse updateMemberRole(Long groupId, Long studentId, GroupMemberRequest request, Long userId);
    
    // Group Assignment Management
    void assignGroupGrade(Long groupId, Long gradeTypeId, Double score, String feedback, Long lecturerId, String lecturerName);
}