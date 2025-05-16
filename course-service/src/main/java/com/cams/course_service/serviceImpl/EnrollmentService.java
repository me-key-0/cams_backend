package com.cams.course_service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cams.course_service.model.CourseSession;
import com.cams.course_service.repository.EnrollmentRepository;

@Service
public class EnrollmentService implements com.cams.course_service.service.EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public List<CourseSession> getCourseSessions(Long studentId, Integer year, Integer semester, Integer academicYear) {
        return enrollmentRepository.findMatchingCourseSessions(studentId, year, semester, academicYear);
    }
    
}
