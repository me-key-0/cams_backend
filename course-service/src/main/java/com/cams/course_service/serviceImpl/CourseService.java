package com.cams.course_service.serviceImpl;

import com.cams.course_service.model.Course;
import com.cams.course_service.model.CourseSession;
import com.cams.course_service.repository.CourseRepository;
import com.cams.course_service.repository.CourseSessionRepository;
import com.cams.course_service.repository.EnrollmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cams.course_service.serviceImpl.CourseService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
public class CourseService implements com.cams.course_service.service.CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSessionRepository courseSessionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course updateCourse(Long id, Course updated) {
        Course existing = getCourseById(id);
        existing.setName(updated.getName());
        existing.setCode(updated.getCode());
        existing.setCreditHour(updated.getCreditHour());
        existing.setDescription(updated.getDescription());
        existing.setPrerequisites(updated.getPrerequisites());
        return courseRepository.save(existing);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> getCoursesByDepartment(Long departmentId) {
        return null;
    }




    
    

    
}
