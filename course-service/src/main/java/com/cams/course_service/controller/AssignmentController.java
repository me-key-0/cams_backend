package com.cams.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cams.course_service.model.CourseSession;
import com.cams.course_service.service.AssignmentService;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {
    
    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/sessions/{lectureId}")
    public List<CourseSession> getStudentCourseSessions(
            @PathVariable Long lectureId) {
        return assignmentService.getCourseSessions(lectureId);
    }

    @GetMapping("/lecturer/{lecturerId}")
    public ResponseEntity<List<CourseSession>> getLecturerCourseSessions(@PathVariable Long lecturerId) {
        List<CourseSession> sessions = assignmentService.getCourseSessions(lecturerId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/lecturer/{lecturerId}/validate/{courseSessionId}")
    public ResponseEntity<Boolean> validateLecturerForCourseSession(
            @PathVariable Long lecturerId,
            @PathVariable Long courseSessionId) {
        List<CourseSession> lecturerSessions = assignmentService.getCourseSessions(lecturerId);
        boolean hasAccess = lecturerSessions.stream()
            .anyMatch(session -> session.getId().equals(courseSessionId));
        return ResponseEntity.ok(hasAccess);
    }
}
