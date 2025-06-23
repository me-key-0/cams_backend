package com.cams.course_service.service.impl;

import com.cams.course_service.client.UserServiceClient;
import com.cams.course_service.dto.*;
import com.cams.course_service.model.Assignment;
import com.cams.course_service.model.Course;
import com.cams.course_service.model.CourseSession;
import com.cams.course_service.model.LecturerCourseCapacity;
import com.cams.course_service.repository.CourseRepository;
import com.cams.course_service.repository.CourseSessionRepository;
import com.cams.course_service.repository.EnrollmentRepository;
import com.cams.course_service.repository.LecturerCourseCapacityRepository;
import com.cams.course_service.service.CourseSessionManagementService;
import com.cams.course_service.service.LecturerManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSessionManagementServiceImpl implements CourseSessionManagementService {

    private final CourseSessionRepository courseSessionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LecturerCourseCapacityRepository lecturerCapacityRepository;
    private final LecturerManagementService lecturerManagementService;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public CourseSessionResponse createCourseSession(CourseSessionRequest request, Long adminId) {
        // Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, request.getDepartmentId());
        
        // Validate lecturers
        if (request.getLecturerIds() != null && !request.getLecturerIds().isEmpty()) {
            for (Long lecturerId : request.getLecturerIds()) {
                if (!validateLecturerCapacity(lecturerId, course.getId())) {
                    throw new IllegalArgumentException("Lecturer " + lecturerId + 
                        " does not have capacity to teach this course or is not authorized to teach it");
                }
            }
        }
        
        CourseSession courseSession = new CourseSession();
        courseSession.setAcademicYear(request.getAcademicYear());
        courseSession.setSemester(request.getSemester());
        courseSession.setYear(request.getYear());
        courseSession.setCourse(course);
        courseSession.setDepartmentId(request.getDepartmentId());
        courseSession.setLecturerId(request.getLecturerIds() != null ? request.getLecturerIds() : new ArrayList<>());
        courseSession.setStatus(Assignment.Status.UPCOMING);
        courseSession.setIsActive(false);
        courseSession.setEnrollmentOpen(false);
        courseSession.setCreatedBy(adminId);
        
        CourseSession savedSession = courseSessionRepository.save(courseSession);
        log.info("Course session created: {} by admin: {}", savedSession.getId(), adminId);
        
        // Update lecturer credit hours if lecturers are assigned
        if (request.getLecturerIds() != null && !request.getLecturerIds().isEmpty()) {
            updateLecturerCreditHours(request.getLecturerIds(), course.getCreditHour(), true);
        }
        
        return convertToCourseSessionResponse(savedSession);
    }

    @Override
    @Transactional
    public CourseSessionResponse updateCourseSession(Long id, CourseSessionRequest request, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        // If course is being changed, validate new course
        if (!courseSession.getCourse().getId().equals(request.getCourseId())) {
            Course newCourse = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            
            // Update lecturer credit hours for old course (subtract)
            if (courseSession.getLecturerId() != null && !courseSession.getLecturerId().isEmpty()) {
                updateLecturerCreditHours(courseSession.getLecturerId(), courseSession.getCourse().getCreditHour(), false);
            }
            
            courseSession.setCourse(newCourse);
            
            // Update lecturer credit hours for new course (add)
            if (courseSession.getLecturerId() != null && !courseSession.getLecturerId().isEmpty()) {
                updateLecturerCreditHours(courseSession.getLecturerId(), newCourse.getCreditHour(), true);
            }
        }
        
        // Update other fields
        courseSession.setAcademicYear(request.getAcademicYear());
        courseSession.setSemester(request.getSemester());
        courseSession.setYear(request.getYear());
        courseSession.setDepartmentId(request.getDepartmentId());
        
        // Handle lecturer changes
        if (request.getLecturerIds() != null) {
            // Remove old lecturers' credit hours
            if (courseSession.getLecturerId() != null && !courseSession.getLecturerId().isEmpty()) {
                updateLecturerCreditHours(courseSession.getLecturerId(), courseSession.getCourse().getCreditHour(), false);
            }
            
            // Validate new lecturers
            for (Long lecturerId : request.getLecturerIds()) {
                if (!validateLecturerCapacity(lecturerId, courseSession.getCourse().getId())) {
                    throw new IllegalArgumentException("Lecturer " + lecturerId + 
                        " does not have capacity to teach this course or is not authorized to teach it");
                }
            }
            
            // Add new lecturers' credit hours
            updateLecturerCreditHours(request.getLecturerIds(), courseSession.getCourse().getCreditHour(), true);
            
            courseSession.setLecturerId(request.getLecturerIds());
        }
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Course session updated: {} by admin: {}", updatedSession.getId(), adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public void deleteCourseSession(Long id, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        // Check if there are enrollments
        long enrollmentCount = enrollmentRepository.findByCourseSessionId(id).size();
        if (enrollmentCount > 0) {
            throw new IllegalStateException("Cannot delete course session with existing enrollments");
        }
        
        // Update lecturer credit hours
        if (courseSession.getLecturerId() != null && !courseSession.getLecturerId().isEmpty()) {
            updateLecturerCreditHours(courseSession.getLecturerId(), courseSession.getCourse().getCreditHour(), false);
        }
        
        // Delete course session
        courseSessionRepository.deleteById(id);
        log.info("Course session deleted: {} by admin: {}", id, adminId);
    }

    @Override
    public CourseSessionResponse getCourseSessionById(Long id) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        return convertToCourseSessionResponse(courseSession);
    }

    @Override
    public List<CourseSessionResponse> getCourseSessionsByDepartment(Long departmentId) {
        // This would need a custom query in the repository
        // For now, we'll filter all course sessions
        List<CourseSession> sessions = courseSessionRepository.findAll().stream()
            .filter(session -> session.getDepartmentId().equals(departmentId))
            .collect(Collectors.toList());
        
        return sessions.stream()
            .map(this::convertToCourseSessionResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseSessionResponse activateCourseSession(Long id, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        courseSession.setIsActive(true);
        courseSession.setActivatedAt(LocalDateTime.now());
        courseSession.setStatus(Assignment.Status.ACTIVE);
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Course session activated: {} by admin: {}", id, adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public CourseSessionResponse deactivateCourseSession(Long id, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        courseSession.setIsActive(false);
        courseSession.setEnrollmentOpen(false);
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Course session deactivated: {} by admin: {}", id, adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public CourseSessionResponse openEnrollment(Long id, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        // Session must be active to open enrollment
        if (!courseSession.getIsActive()) {
            throw new IllegalStateException("Course session must be active to open enrollment");
        }
        
        courseSession.setEnrollmentOpen(true);
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Course session enrollment opened: {} by admin: {}", id, adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public CourseSessionResponse closeEnrollment(Long id, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        courseSession.setEnrollmentOpen(false);
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Course session enrollment closed: {} by admin: {}", id, adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public CourseSessionResponse assignLecturers(Long id, List<Long> lecturerIds, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        // Remove old lecturers' credit hours
        if (courseSession.getLecturerId() != null && !courseSession.getLecturerId().isEmpty()) {
            updateLecturerCreditHours(courseSession.getLecturerId(), courseSession.getCourse().getCreditHour(), false);
        }
        
        // Validate new lecturers
        for (Long lecturerId : lecturerIds) {
            if (!validateLecturerCapacity(lecturerId, courseSession.getCourse().getId())) {
                throw new IllegalArgumentException("Lecturer " + lecturerId + 
                    " does not have capacity to teach this course or is not authorized to teach it");
            }
        }
        
        // Add new lecturers' credit hours
        updateLecturerCreditHours(lecturerIds, courseSession.getCourse().getCreditHour(), true);
        
        courseSession.setLecturerId(lecturerIds);
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Lecturers assigned to course session: {} by admin: {}", id, adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public CourseSessionResponse removeLecturer(Long id, Long lecturerId, Long adminId) {
        CourseSession courseSession = courseSessionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, courseSession.getDepartmentId());
        
        // Check if lecturer is assigned to this session
        if (courseSession.getLecturerId() == null || !courseSession.getLecturerId().contains(lecturerId)) {
            throw new IllegalArgumentException("Lecturer is not assigned to this course session");
        }
        
        // Remove lecturer
        List<Long> updatedLecturerIds = new ArrayList<>(courseSession.getLecturerId());
        updatedLecturerIds.remove(lecturerId);
        courseSession.setLecturerId(updatedLecturerIds);
        
        // Update lecturer credit hours
        updateLecturerCreditHours(List.of(lecturerId), courseSession.getCourse().getCreditHour(), false);
        
        CourseSession updatedSession = courseSessionRepository.save(courseSession);
        log.info("Lecturer {} removed from course session: {} by admin: {}", lecturerId, id, adminId);
        
        return convertToCourseSessionResponse(updatedSession);
    }

    @Override
    public boolean validateLecturerCapacity(Long lecturerId, Long courseId) {
        // Check if lecturer is authorized to teach this course
        if (!lecturerManagementService.validateLecturerForCourse(lecturerId, courseId)) {
            return false;
        }
        
        // Get course credit hours
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Get lecturer capacity
        LecturerCourseCapacity capacity = lecturerCapacityRepository.findByLecturerIdAndIsActiveTrue(lecturerId)
            .orElseThrow(() -> new IllegalArgumentException("Lecturer capacity not found"));
        
        // Check if adding this course would exceed capacity
        return (capacity.getCurrentCreditHours() + course.getCreditHour()) <= capacity.getMaxCreditHours();
    }

    // Helper methods
    private void validateDepartmentAccess(Long adminId, Long departmentId) {
        // In a real implementation, this would check if the admin has access to this department
        // For now, we'll just log the validation
        log.debug("Validating admin {} access to department {}", adminId, departmentId);
    }

    private CourseSessionResponse convertToCourseSessionResponse(CourseSession session) {
        CourseSessionResponse response = new CourseSessionResponse();
        response.setId(session.getId());
        response.setAcademicYear(session.getAcademicYear());
        response.setSemester(session.getSemester());
        response.setYear(session.getYear());
        
        CourseDto courseDto = new CourseDto();
        courseDto.setName(session.getCourse().getName());
        courseDto.setCode(session.getCourse().getCode());
        courseDto.setCreditHour(session.getCourse().getCreditHour());
        response.setCourse(courseDto);
        
        response.setDepartmentId(session.getDepartmentId());
        response.setStatus(session.getStatus());
        response.setIsActive(session.getIsActive());
        response.setEnrollmentOpen(session.getEnrollmentOpen());
        response.setCreatedAt(session.getCreatedAt());
        response.setActivatedAt(session.getActivatedAt());
        response.setCreatedBy(session.getCreatedBy());
        
        // Get enrolled students count
        int enrolledStudents = enrollmentRepository.findByCourseSessionId(session.getId()).size();
        response.setEnrolledStudents(enrolledStudents);
        
        // Get lecturer information
        List<CourseSessionResponse.LecturerInfo> lecturers = new ArrayList<>();
        if (session.getLecturerId() != null) {
            for (Long lecturerId : session.getLecturerId()) {
                try {
                    // In a real implementation, this would fetch lecturer details from user service
                    CourseSessionResponse.LecturerInfo lecturerInfo = new CourseSessionResponse.LecturerInfo();
                    lecturerInfo.setId(lecturerId);
                    lecturerInfo.setName("Lecturer " + lecturerId);
                    lecturerInfo.setEmail("lecturer" + lecturerId + "@example.com");
                    lecturers.add(lecturerInfo);
                } catch (Exception e) {
                    log.error("Error fetching lecturer details for ID: {}", lecturerId, e);
                }
            }
        }
        response.setLecturers(lecturers);
        
        return response;
    }

    private void updateLecturerCreditHours(List<Long> lecturerIds, int creditHours, boolean isAdding) {
        for (Long lecturerId : lecturerIds) {
            LecturerCourseCapacity capacity = lecturerCapacityRepository.findByLecturerIdAndIsActiveTrue(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer capacity not found for lecturer: " + lecturerId));
            
            if (isAdding) {
                capacity.setCurrentCreditHours(capacity.getCurrentCreditHours() + creditHours);
            } else {
                capacity.setCurrentCreditHours(Math.max(0, capacity.getCurrentCreditHours() - creditHours));
            }
            
            lecturerCapacityRepository.save(capacity);
        }
    }
}