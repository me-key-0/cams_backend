package com.cams.grade_service.controller;

import com.cams.grade_service.dto.*;
import com.cams.grade_service.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // Group Management
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @RequestBody GroupRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        String userName = "User " + userId; // Should be fetched from user service
        GroupResponse group = groupService.createGroup(request, Long.parseLong(userId), userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long id,
            @RequestBody GroupRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        GroupResponse group = groupService.updateGroup(id, request, Long.parseLong(userId));
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        groupService.deleteGroup(id, Long.parseLong(userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long id) {
        GroupResponse group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/course-session/{courseSessionId}")
    public ResponseEntity<List<GroupResponse>> getGroupsByCourseSession(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Role") String role) {
        
        List<GroupResponse> groups = groupService.getGroupsByCourseSession(courseSessionId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupResponse>> getMyGroups(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        List<GroupResponse> groups = groupService.getMyGroups(Long.parseLong(userId));
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/student/{studentId}/course-session/{courseSessionId}")
    public ResponseEntity<List<GroupResponse>> getStudentGroups(
            @PathVariable Long studentId,
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Students can only view their own groups
        if ("STUDENT".equals(role) && !userId.equals(studentId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<GroupResponse> groups = groupService.getStudentGroups(studentId, courseSessionId);
        return ResponseEntity.ok(groups);
    }

    // Group Member Management
    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupMemberResponse> addMemberToGroup(
            @PathVariable Long groupId,
            @RequestBody GroupMemberRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        GroupMemberResponse member = groupService.addMemberToGroup(groupId, request, Long.parseLong(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @DeleteMapping("/{groupId}/members/{studentId}")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        groupService.removeMemberFromGroup(groupId, studentId, Long.parseLong(userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMemberResponse> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{groupId}/members/{studentId}")
    public ResponseEntity<GroupMemberResponse> updateMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long studentId,
            @RequestBody GroupMemberRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        GroupMemberResponse member = groupService.updateMemberRole(groupId, studentId, request, Long.parseLong(userId));
        return ResponseEntity.ok(member);
    }

    // Group Grading
    @PostMapping("/{groupId}/grade")
    public ResponseEntity<Void> assignGroupGrade(
            @PathVariable Long groupId,
            @RequestParam Long gradeTypeId,
            @RequestParam Double score,
            @RequestParam(required = false) String feedback,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String lecturerName = "Lecturer " + lecturerId; // Should be fetched from user service
        groupService.assignGroupGrade(groupId, gradeTypeId, score, feedback, Long.parseLong(lecturerId), lecturerName);
        return ResponseEntity.ok().build();
    }
}