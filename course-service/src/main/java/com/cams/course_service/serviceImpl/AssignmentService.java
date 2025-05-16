package com.cams.course_service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cams.course_service.model.Assignment.Status;
import com.cams.course_service.repository.AssignmentRepository;
import com.cams.course_service.model.Assignment;
import com.cams.course_service.model.CourseSession;

@Service
public class AssignmentService implements com.cams.course_service.service.AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Override
    public List<CourseSession> getCourseSessions(Long lectureId) {
        List<Assignment> assignments = assignmentRepository.findByLecturerIdAndStatus(lectureId,Status.ACTIVE);

        return assignments.stream().map(Assignment::getCourseSession).collect(Collectors.toList());
    }
    
}
