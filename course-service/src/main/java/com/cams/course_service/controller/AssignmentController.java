package com.cams.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cams.course_service.model.CourseSession;
import com.cams.course_service.serviceImpl.AssignmentService;

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
}
