package com.cams.course_service.service.impl;

import com.cams.course_service.client.UserServiceClient;
import com.cams.course_service.dto.*;
import com.cams.course_service.model.Course;
import com.cams.course_service.model.LecturerCourseCapacity;
import com.cams.course_service.model.LecturerTeachableCourse;
import com.cams.course_service.repository.CourseRepository;
import com.cams.course_service.repository.LecturerCourseCapacityRepository;
import com.cams.course_service.repository.LecturerTeachableCourseRepository;
import com.cams.course_service.service.LecturerManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LecturerManagementServiceImpl implements LecturerManagementService {

    private final LecturerCourseCapacityRepository capacityRepository;
    private final LecturerTeachableCourseRepository teachableCourseRepository;
    private final CourseRepository courseRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public LecturerCapacityResponse setLecturerCapacity(LecturerCapacityRequest request, Long adminId) {
        // Validate department access
        validateDepartmentAccess(adminId, request.getDepartmentId());
        
        // Validate lecturer exists and belongs to the department
        validateLecturerDepartment(request.getLecturerId(), request.getDepartmentId());
        
        // Check if capacity already exists
        LecturerCourseCapacity capacity = capacityRepository.findByLecturerIdAndIsActiveTrue(request.getLecturerId())
            .orElse(new LecturerCourseCapacity());
        
        capacity.setLecturerId(request.getLecturerId());
        capacity.setDepartmentId(request.getDepartmentId());
        capacity.setMaxCreditHours(request.getMaxCreditHours());
        
        // Initialize current credit hours if new
        if (capacity.getCurrentCreditHours() == null) {
            capacity.setCurrentCreditHours(0);
        }
        
        capacity.setIsActive(true);
        
        LecturerCourseCapacity savedCapacity = capacityRepository.save(capacity);
        log.info("Lecturer capacity set for lecturer: {} by admin: {}", request.getLecturerId(), adminId);
        
        return convertToLecturerCapacityResponse(savedCapacity);
    }

    @Override
    public LecturerCapacityResponse getLecturerCapacity(Long lecturerId) {
        LecturerCourseCapacity capacity = capacityRepository.findByLecturerIdAndIsActiveTrue(lecturerId)
            .orElseThrow(() -> new IllegalArgumentException("Lecturer capacity not found"));
        
        return convertToLecturerCapacityResponse(capacity);
    }

    @Override
    public List<LecturerCapacityResponse> getLecturerCapacitiesByDepartment(Long departmentId) {
        List<LecturerCourseCapacity> capacities = capacityRepository
            .findByDepartmentIdAndIsActiveTrueOrderByLecturerIdAsc(departmentId);
        
        return capacities.stream()
            .map(this::convertToLecturerCapacityResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseDto> assignTeachableCourses(LecturerTeachableCoursesRequest request, Long adminId) {
        // Get lecturer capacity to validate department
        LecturerCourseCapacity capacity = capacityRepository.findByLecturerIdAndIsActiveTrue(request.getLecturerId())
            .orElseThrow(() -> new IllegalArgumentException("Lecturer capacity not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, capacity.getDepartmentId());
        
        List<CourseDto> assignedCourses = new ArrayList<>();
        
        for (Long courseId : request.getCourseIds()) {
            // Validate course exists
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
            
            // Validate course belongs to the same department as lecturer
            if (!course.getDepartmentId().equals(capacity.getDepartmentId().toString())) {
                throw new IllegalArgumentException("Course " + course.getCode() + 
                    " does not belong to the lecturer's department");
            }
            
            // Check if already assigned
            if (teachableCourseRepository.existsByLecturerIdAndCourseIdAndIsActiveTrue(
                request.getLecturerId(), courseId)) {
                continue; // Skip if already assigned
            }
            
            // Create teachable course
            LecturerTeachableCourse teachableCourse = LecturerTeachableCourse.builder()
                .lecturerId(request.getLecturerId())
                .course(course)
                .isActive(true)
                .build();
            
            teachableCourseRepository.save(teachableCourse);
            log.info("Course {} assigned as teachable to lecturer {}", courseId, request.getLecturerId());
            
            CourseDto courseDto = new CourseDto();
            courseDto.setName(course.getName());
            courseDto.setCode(course.getCode());
            courseDto.setCreditHour(course.getCreditHour());
            assignedCourses.add(courseDto);
        }
        
        return assignedCourses;
    }

    @Override
    public List<CourseDto> getTeachableCourses(Long lecturerId) {
        List<LecturerTeachableCourse> teachableCourses = teachableCourseRepository
            .findByLecturerIdAndIsActiveTrueOrderByAssignedAtDesc(lecturerId);
        
        return teachableCourses.stream()
            .map(tc -> {
                CourseDto dto = new CourseDto();
                dto.setName(tc.getCourse().getName());
                dto.setCode(tc.getCourse().getCode());
                dto.setCreditHour(tc.getCourse().getCreditHour());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeTeachableCourse(Long lecturerId, Long courseId, Long adminId) {
        // Get lecturer capacity to validate department
        LecturerCourseCapacity capacity = capacityRepository.findByLecturerIdAndIsActiveTrue(lecturerId)
            .orElseThrow(() -> new IllegalArgumentException("Lecturer capacity not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, capacity.getDepartmentId());
        
        // Delete teachable course
        teachableCourseRepository.deleteByLecturerIdAndCourseId(lecturerId, courseId);
        log.info("Teachable course {} removed from lecturer {} by admin {}", courseId, lecturerId, adminId);
    }

    @Override
    public boolean validateLecturerForCourse(Long lecturerId, Long courseId) {
        return teachableCourseRepository.existsByLecturerIdAndCourseIdAndIsActiveTrue(lecturerId, courseId);
    }

    @Override
    public boolean validateLecturerDepartment(Long lecturerId, Long departmentId) {
        // In a real implementation, this would check if the lecturer belongs to the department
        // using the user service
        // For now, we'll just log the validation
        log.debug("Validating lecturer {} belongs to department {}", lecturerId, departmentId);
        return true;
    }

    // Helper methods
    private void validateDepartmentAccess(Long adminId, Long departmentId) {
        // In a real implementation, this would check if the admin has access to this department
        // For now, we'll just log the validation
        log.debug("Validating admin {} access to department {}", adminId, departmentId);
    }

    private LecturerCapacityResponse convertToLecturerCapacityResponse(LecturerCourseCapacity capacity) {
        LecturerCapacityResponse response = new LecturerCapacityResponse();
        response.setId(capacity.getId());
        response.setLecturerId(capacity.getLecturerId());
        
        // In a real implementation, fetch lecturer name from user service
        response.setLecturerName("Lecturer " + capacity.getLecturerId());
        
        response.setDepartmentId(capacity.getDepartmentId());
        response.setMaxCreditHours(capacity.getMaxCreditHours());
        response.setCurrentCreditHours(capacity.getCurrentCreditHours());
        response.setAvailableCreditHours(capacity.getMaxCreditHours() - capacity.getCurrentCreditHours());
        response.setIsActive(capacity.getIsActive());
        
        return response;
    }
}