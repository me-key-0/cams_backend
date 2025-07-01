package com.cams.grade_service.service.impl;

import com.cams.grade_service.dto.*;
import com.cams.grade_service.model.*;
import com.cams.grade_service.repository.*;
import com.cams.grade_service.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final StudentGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final StudentGradeRepository gradeRepository;
    private final GradeTypeRepository gradeTypeRepository;

    @Override
    @Transactional
    public GroupResponse createGroup(GroupRequest request, Long createdBy, String createdByName) {
        StudentGroup group = StudentGroup.builder()
            .name(request.getName())
            .description(request.getDescription())
            .courseSessionId(request.getCourseSessionId())
            .createdBy(createdBy)
            .createdByName(createdByName)
            .type(request.getType())
            .isActive(true)
            .build();
        
        StudentGroup savedGroup = groupRepository.save(group);
        
        // Add members
        if (request.getMembers() != null && !request.getMembers().isEmpty()) {
            for (GroupMemberRequest memberRequest : request.getMembers()) {
                GroupMember member = GroupMember.builder()
                    .group(savedGroup)
                    .studentId(memberRequest.getStudentId())
                    .studentName(memberRequest.getStudentName())
                    .role(memberRequest.getRole())
                    .build();
                memberRepository.save(member);
            }
        }
        
        log.info("Group created: {} by user: {}", savedGroup.getId(), createdBy);
        return convertToGroupResponse(savedGroup);
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(Long id, GroupRequest request, Long userId) {
        StudentGroup group = groupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check permissions
        if (!canManageGroup(group, userId)) {
            throw new RuntimeException("Access denied");
        }
        
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        
        StudentGroup updatedGroup = groupRepository.save(group);
        return convertToGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public void deleteGroup(Long id, Long userId) {
        StudentGroup group = groupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!canManageGroup(group, userId)) {
            throw new RuntimeException("Access denied");
        }
        
        group.setIsActive(false);
        groupRepository.save(group);
    }

    @Override
    public GroupResponse getGroupById(Long id) {
        StudentGroup group = groupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        return convertToGroupResponse(group);
    }

    @Override
    public List<GroupResponse> getGroupsByCourseSession(Long courseSessionId) {
        List<StudentGroup> groups = groupRepository.findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtDesc(courseSessionId);
        return groups.stream()
            .map(this::convertToGroupResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponse> getMyGroups(Long userId) {
        List<StudentGroup> groups = groupRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(userId);
        return groups.stream()
            .map(this::convertToGroupResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponse> getStudentGroups(Long studentId, Long courseSessionId) {
        List<GroupMember> memberships = memberRepository.findByStudentAndCourseSession(studentId, courseSessionId);
        return memberships.stream()
            .map(member -> convertToGroupResponse(member.getGroup()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupMemberResponse addMemberToGroup(Long groupId, GroupMemberRequest request, Long userId) {
        StudentGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!canManageGroup(group, userId)) {
            throw new RuntimeException("Access denied");
        }
        
        // Check if student is already a member
        if (memberRepository.findByGroupIdAndStudentId(groupId, request.getStudentId()).isPresent()) {
            throw new RuntimeException("Student is already a member of this group");
        }
        
        GroupMember member = GroupMember.builder()
            .group(group)
            .studentId(request.getStudentId())
            .studentName(request.getStudentName())
            .role(request.getRole())
            .build();
        
        GroupMember savedMember = memberRepository.save(member);
        return convertToGroupMemberResponse(savedMember);
    }

    @Override
    @Transactional
    public void removeMemberFromGroup(Long groupId, Long studentId, Long userId) {
        StudentGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!canManageGroup(group, userId) && !userId.equals(studentId)) {
            throw new RuntimeException("Access denied");
        }
        
        memberRepository.deleteByGroupIdAndStudentId(groupId, studentId);
    }

    @Override
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        List<GroupMember> members = memberRepository.findByGroupIdOrderByJoinedAtAsc(groupId);
        return members.stream()
            .map(this::convertToGroupMemberResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupMemberResponse updateMemberRole(Long groupId, Long studentId, GroupMemberRequest request, Long userId) {
        StudentGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!canManageGroup(group, userId)) {
            throw new RuntimeException("Access denied");
        }
        
        GroupMember member = memberRepository.findByGroupIdAndStudentId(groupId, studentId)
            .orElseThrow(() -> new RuntimeException("Member not found"));
        
        member.setRole(request.getRole());
        GroupMember updatedMember = memberRepository.save(member);
        
        return convertToGroupMemberResponse(updatedMember);
    }

    @Override
    @Transactional
    public void assignGroupGrade(Long groupId, Long gradeTypeId, Double score, String feedback, Long lecturerId, String lecturerName) {
        StudentGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        GradeType gradeType = gradeTypeRepository.findById(gradeTypeId)
            .orElseThrow(() -> new RuntimeException("Grade type not found"));
        
        List<GroupMember> members = memberRepository.findByGroupIdOrderByJoinedAtAsc(groupId);
        
        for (GroupMember member : members) {
            StudentGrade grade = StudentGrade.builder()
                .studentId(member.getStudentId())
                .studentName(member.getStudentName())
                .gradeType(gradeType)
                .score(score)
                .feedback(feedback)
                .gradedBy(lecturerId)
                .graderName(lecturerName)
                .groupId(groupId)
                .build();
            
            gradeRepository.save(grade);
        }
        
        log.info("Group grade assigned: group {} for grade type {}", groupId, gradeTypeId);
    }

    // Helper methods
    private boolean canManageGroup(StudentGroup group, Long userId) {
        // Group creator can manage
        if (group.getCreatedBy().equals(userId)) {
            return true;
        }
        
        // Group leader can manage (for student-created groups)
        if (group.getType() == StudentGroup.GroupType.STUDENT_CREATED) {
            return memberRepository.findByGroupIdAndStudentId(group.getId(), userId)
                .map(member -> member.getRole() == GroupMember.MemberRole.LEADER)
                .orElse(false);
        }
        
        return false;
    }

    private GroupResponse convertToGroupResponse(StudentGroup group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setCourseSessionId(group.getCourseSessionId());
        response.setCreatedBy(group.getCreatedBy());
        response.setCreatedByName(group.getCreatedByName());
        response.setType(group.getType());
        response.setCreatedAt(group.getCreatedAt());
        response.setIsActive(group.getIsActive());
        
        // Load members
        List<GroupMember> members = memberRepository.findByGroupIdOrderByJoinedAtAsc(group.getId());
        response.setMembers(members.stream()
            .map(this::convertToGroupMemberResponse)
            .collect(Collectors.toList()));
        response.setMemberCount(members.size());
        
        return response;
    }

    private GroupMemberResponse convertToGroupMemberResponse(GroupMember member) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setId(member.getId());
        response.setStudentId(member.getStudentId());
        response.setStudentName(member.getStudentName());
        response.setRole(member.getRole());
        response.setJoinedAt(member.getJoinedAt());
        return response;
    }
}