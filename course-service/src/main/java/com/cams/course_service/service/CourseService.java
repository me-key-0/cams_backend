package com.cams.course_service.service;

import com.cams.course_service.model.Course;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    Course getCourseById(Long id);
    List<Course> getAllCourses();
    Course updateCourse(Long id, Course updated);
    void deleteCourse(Long id);
    List<Course> getCoursesByDepartment(Long departmentId);
    // List<CourseSession> getCourseSessions(Long studentId, Integer year, Integer semester, Integer academicYear);
}

