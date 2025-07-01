package com.cams.course_service.service.impl;

import com.cams.course_service.dto.*;
import com.cams.course_service.model.Batch;
import com.cams.course_service.model.BatchCourseAssignment;
import com.cams.course_service.model.Course;
import com.cams.course_service.model.CourseSession;
import com.cams.course_service.repository.BatchCourseAssignmentRepository;
import com.cams.course_service.repository.BatchRepository;
import com.cams.course_service.repository.CourseRepository;
import com.cams.course_service.repository.CourseSessionRepository;
import com.cams.course_service.service.BatchService;
import com.cams.course_service.service.CourseSessionManagementService;
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
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final BatchCourseAssignmentRepository batchCourseAssignmentRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final CourseSessionManagementService courseSessionService;
    
    // Maximum credit hours per semester
    private static final int MAX_CREDIT_HOURS_PER_SEMESTER = 24;

    @Override
    @Transactional
    public BatchResponse createBatch(BatchRequest request, Long adminId) {
        // Validate batch name uniqueness in department
        if (batchRepository.existsByNameAndDepartmentId(request.getName(), request.getDepartmentId())) {
            throw new IllegalArgumentException("Batch with this name already exists in the department");
        }
        
        Batch batch = Batch.builder()
            .name(request.getName())
            .admissionYear(request.getAdmissionYear())
            .currentYear(request.getCurrentYear())
            .currentSemester(request.getCurrentSemester())
            .departmentId(request.getDepartmentId())
            .isActive(true)
            .build();
        
        Batch savedBatch = batchRepository.save(batch);
        log.info("Batch created: {} by admin: {}", savedBatch.getId(), adminId);
        
        return convertToBatchResponse(savedBatch);
    }

    @Override
    @Transactional
    public BatchResponse updateBatch(Long id, BatchRequest request, Long adminId) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, batch.getDepartmentId());
        
        // Check if name is being changed and validate uniqueness
        if (!batch.getName().equals(request.getName()) && 
            batchRepository.existsByNameAndDepartmentId(request.getName(), batch.getDepartmentId())) {
            throw new IllegalArgumentException("Batch with this name already exists in the department");
        }
        
        batch.setName(request.getName());
        batch.setAdmissionYear(request.getAdmissionYear());
        batch.setCurrentYear(request.getCurrentYear());
        batch.setCurrentSemester(request.getCurrentSemester());
        
        Batch updatedBatch = batchRepository.save(batch);
        log.info("Batch updated: {} by admin: {}", updatedBatch.getId(), adminId);
        
        return convertToBatchResponse(updatedBatch);
    }

    @Override
    @Transactional
    public void deleteBatch(Long id, Long adminId) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, batch.getDepartmentId());
        
        // Soft delete
        batch.setIsActive(false);
        batchRepository.save(batch);
        log.info("Batch deleted: {} by admin: {}", id, adminId);
    }

    @Override
    public BatchResponse getBatchById(Long id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found"));
        return convertToBatchResponse(batch);
    }

    @Override
    public List<BatchResponse> getBatchesByDepartment(Long departmentId) {
        List<Batch> batches = batchRepository.findByDepartmentIdAndIsActiveTrueOrderByAdmissionYearDesc(departmentId);
        return batches.stream()
            .map(this::convertToBatchResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseAssignmentResponse> assignCoursesToBatch(CourseAssignmentRequest request, Long adminId) {
        Batch batch = batchRepository.findById(request.getBatchId())
            .orElseThrow(() -> new IllegalArgumentException("Batch not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, batch.getDepartmentId());
        
        List<CourseAssignmentResponse> responses = new ArrayList<>();
        
        for (CourseAssignmentRequest.CourseAssignmentItem item : request.getCourses()) {
            // Validate course exists
            Course course = courseRepository.findById(item.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + item.getCourseId()));
            
            // Check if course is already assigned to this batch
            if (batchCourseAssignmentRepository.existsByBatchIdAndCourseIdAndIsActiveTrue(batch.getId(), course.getId())) {
                throw new IllegalArgumentException("Course " + course.getCode() + " is already assigned to this batch");
            }
            
            // Validate credit hours limit
            Integer currentTotalCredits = batchCourseAssignmentRepository.getTotalCreditHoursForBatchSemester(
                batch.getId(), item.getYear(), item.getSemester());
            
            if (currentTotalCredits == null) {
                currentTotalCredits = 0;
            }
            
            if (currentTotalCredits + course.getCreditHour() > MAX_CREDIT_HOURS_PER_SEMESTER) {
                throw new IllegalArgumentException("Adding this course would exceed the maximum credit hours (" + 
                    MAX_CREDIT_HOURS_PER_SEMESTER + ") for year " + item.getYear() + ", semester " + item.getSemester());
            }
            
            // Create assignment
            BatchCourseAssignment assignment = BatchCourseAssignment.builder()
                .batch(batch)
                .course(course)
                .year(item.getYear())
                .semester(item.getSemester())
                .assignedBy(adminId)
                .isActive(true)
                .build();
            
            BatchCourseAssignment savedAssignment = batchCourseAssignmentRepository.save(assignment);
            log.info("Course {} assigned to batch {} by admin {}", course.getId(), batch.getId(), adminId);
            
            // Create a course session for this assignment
            CourseSessionRequest sessionRequest = new CourseSessionRequest();
            sessionRequest.setAcademicYear(batch.getAdmissionYear() + item.getYear() - 1); // Calculate academic year
            sessionRequest.setSemester(item.getSemester());
            sessionRequest.setYear(item.getYear());
            sessionRequest.setCourseId(course.getId());
            sessionRequest.setDepartmentId(batch.getDepartmentId());
            sessionRequest.setBatchId(batch.getId());
            
            try {
                courseSessionService.createCourseSession(sessionRequest, adminId);
                log.info("Course session created for batch {} course {} by admin {}", 
                    batch.getId(), course.getId(), adminId);
            } catch (Exception e) {
                log.error("Failed to create course session for batch {} course {}: {}", 
                    batch.getId(), course.getId(), e.getMessage());
            }
            
            responses.add(convertToCourseAssignmentResponse(savedAssignment));
        }
        
        return responses;
    }

    @Override
    public List<CourseAssignmentResponse> getCourseAssignmentsForBatch(Long batchId) {
        List<BatchCourseAssignment> assignments = batchCourseAssignmentRepository
            .findByBatchIdAndIsActiveTrueOrderByYearAscSemesterAsc(batchId);
        
        return assignments.stream()
            .map(this::convertToCourseAssignmentResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeCourseAssignment(Long assignmentId, Long adminId) {
        BatchCourseAssignment assignment = batchCourseAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Course assignment not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, assignment.getBatch().getDepartmentId());
        
        // Find and delete associated course sessions
        List<CourseSession> sessions = courseSessionRepository.findByBatchId(assignment.getBatch().getId()).stream()
            .filter(session -> session.getCourse().getId().equals(assignment.getCourse().getId()) &&
                   session.getYear().equals(assignment.getYear()) &&
                   session.getSemester().equals(assignment.getSemester()))
            .collect(Collectors.toList());
        
        for (CourseSession session : sessions) {
            try {
                courseSessionService.deleteCourseSession(session.getId(), adminId);
            } catch (Exception e) {
                log.error("Failed to delete course session {} when removing assignment: {}", 
                    session.getId(), e.getMessage());
            }
        }
        
        // Soft delete
        assignment.setIsActive(false);
        batchCourseAssignmentRepository.save(assignment);
        log.info("Course assignment removed: {} by admin: {}", assignmentId, adminId);
    }

    @Override
    @Transactional
    public BatchResponse advanceBatchSemester(Long batchId, Long adminId) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found"));
        
        // Validate department access
        validateDepartmentAccess(adminId, batch.getDepartmentId());
        
        // Advance semester
        if (batch.getCurrentSemester() == 1) {
            // Move to second semester of current year
            batch.setCurrentSemester(2);
        } else {
            // Move to first semester of next year
            batch.setCurrentSemester(1);
            batch.setCurrentYear(batch.getCurrentYear() + 1);
        }
        
        Batch updatedBatch = batchRepository.save(batch);
        log.info("Batch advanced: {} by admin: {}", batchId, adminId);
        
        return convertToBatchResponse(updatedBatch);
    }

    @Override
    public boolean validateCreditHours(Long batchId, Integer year, Integer semester, List<Long> courseIds) {
        // Get current total credit hours
        Integer currentTotalCredits = batchCourseAssignmentRepository.getTotalCreditHoursForBatchSemester(
            batchId, year, semester);
        
        if (currentTotalCredits == null) {
            currentTotalCredits = 0;
        }
        
        // Calculate total credit hours of new courses
        int newCoursesCredits = 0;
        for (Long courseId : courseIds) {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
            newCoursesCredits += course.getCreditHour();
        }
        
        // Check if total would exceed maximum
        return (currentTotalCredits + newCoursesCredits) <= MAX_CREDIT_HOURS_PER_SEMESTER;
    }

    // Helper methods
    private void validateDepartmentAccess(Long adminId, Long departmentId) {
        // In a real implementation, this would check if the admin has access to this department
        // For now, we'll just log the validation
        log.debug("Validating admin {} access to department {}", adminId, departmentId);
    }

    private BatchResponse convertToBatchResponse(Batch batch) {
        BatchResponse response = new BatchResponse();
        response.setId(batch.getId());
        response.setName(batch.getName());
        response.setAdmissionYear(batch.getAdmissionYear());
        response.setCurrentYear(batch.getCurrentYear());
        response.setCurrentSemester(batch.getCurrentSemester());
        response.setDepartmentId(batch.getDepartmentId());
        response.setCreatedAt(batch.getCreatedAt());
        response.setIsActive(batch.getIsActive());
        
        // Load course assignments
        List<BatchCourseAssignment> assignments = batchCourseAssignmentRepository
            .findByBatchIdAndIsActiveTrueOrderByYearAscSemesterAsc(batch.getId());
        
        response.setCourseAssignments(assignments.stream()
            .map(this::convertToCourseAssignmentResponse)
            .collect(Collectors.toList()));
        
        // In a real implementation, you would fetch the total students from the user service
        response.setTotalStudents(0);
        
        return response;
    }

    private CourseAssignmentResponse convertToCourseAssignmentResponse(BatchCourseAssignment assignment) {
        CourseAssignmentResponse response = new CourseAssignmentResponse();
        response.setId(assignment.getId());
        response.setBatchId(assignment.getBatch().getId());
        response.setBatchName(assignment.getBatch().getName());
        response.setCourseId(assignment.getCourse().getId());
        response.setCourseCode(assignment.getCourse().getCode());
        response.setCourseName(assignment.getCourse().getName());
        response.setCreditHour(assignment.getCourse().getCreditHour());
        response.setYear(assignment.getYear());
        response.setSemester(assignment.getSemester());
        response.setAssignedBy(assignment.getAssignedBy());
        response.setAssignedAt(assignment.getAssignedAt());
        response.setIsActive(assignment.getIsActive());
        return response;
    }
}