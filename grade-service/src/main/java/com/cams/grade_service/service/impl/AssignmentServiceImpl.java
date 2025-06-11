package com.cams.grade_service.service.impl;

import com.cams.grade_service.client.CourseServiceClient;
import com.cams.grade_service.client.CourseSessionDto;
import com.cams.grade_service.client.ResourceDto;
import com.cams.grade_service.client.ResourceServiceClient;
import com.cams.grade_service.dto.*;
import com.cams.grade_service.model.Assignment;
import com.cams.grade_service.model.Submission;
import com.cams.grade_service.repository.AssignmentRepository;
import com.cams.grade_service.repository.SubmissionRepository;
import com.cams.grade_service.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseServiceClient courseServiceClient;
    private final ResourceServiceClient resourceServiceClient;

    @Override
    @Transactional
    public AssignmentResponse createAssignment(AssignmentCreateRequest request, Long lecturerId, String lecturerName) {
        // Validate course session exists and lecturer has access
        validateLecturerAccess(lecturerId, request.getCourseSessionId());
        
        Assignment assignment = Assignment.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .courseSessionId(request.getCourseSessionId())
            .lecturerId(lecturerId)
            .lecturerName(lecturerName)
            .dueDate(request.getDueDate())
            .maxScore(request.getMaxScore())
            .type(request.getType())
            .status(Assignment.AssignmentStatus.DRAFT)
            .attachmentIds(request.getAttachmentIds())
            .build();
        
        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment created: {} by lecturer: {}", savedAssignment.getId(), lecturerId);
        
        return convertToResponse(savedAssignment, lecturerId, "LECTURER");
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(Long id, AssignmentCreateRequest request, Long lecturerId) {
        Assignment assignment = getAssignmentByIdAndLecturer(id, lecturerId);
        
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxScore(request.getMaxScore());
        assignment.setType(request.getType());
        assignment.setAttachmentIds(request.getAttachmentIds());
        
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment updated: {} by lecturer: {}", id, lecturerId);
        
        return convertToResponse(updatedAssignment, lecturerId, "LECTURER");
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id, Long lecturerId) {
        Assignment assignment = getAssignmentByIdAndLecturer(id, lecturerId);
        
        // Check if there are submissions
        int submissionCount = submissionRepository.countByAssignmentId(id);
        if (submissionCount > 0) {
            throw new IllegalStateException("Cannot delete assignment with existing submissions");
        }
        
        assignmentRepository.delete(assignment);
        log.info("Assignment deleted: {} by lecturer: {}", id, lecturerId);
    }

    @Override
    public AssignmentResponse getAssignmentById(Long id, Long userId, String role) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        // Validate access based on role
        if ("LECTURER".equals(role) && !assignment.getLecturerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        if ("STUDENT".equals(role) && assignment.getStatus() != Assignment.AssignmentStatus.PUBLISHED) {
            throw new RuntimeException("Assignment not available");
        }
        
        return convertToResponse(assignment, userId, role);
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByCourseSession(Long courseSessionId, Long userId, String role) {
        Assignment.AssignmentStatus status = "STUDENT".equals(role) ? 
            Assignment.AssignmentStatus.PUBLISHED : null;
        
        List<Assignment> assignments;
        if (status != null) {
            assignments = assignmentRepository.findByCourseSessionIdAndStatusOrderByCreatedAtDesc(courseSessionId, status);
        } else {
            assignments = assignmentRepository.findByCourseSessionIdAndStatusOrderByCreatedAtDesc(courseSessionId, Assignment.AssignmentStatus.PUBLISHED);
            assignments.addAll(assignmentRepository.findByCourseSessionIdAndStatusOrderByCreatedAtDesc(courseSessionId, Assignment.AssignmentStatus.DRAFT));
        }
        
        return assignments.stream()
            .map(assignment -> convertToResponse(assignment, userId, role))
            .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> getMyAssignments(Long lecturerId) {
        List<Assignment> assignments = assignmentRepository.findByLecturerIdOrderByCreatedAtDesc(lecturerId);
        return assignments.stream()
            .map(assignment -> convertToResponse(assignment, lecturerId, "LECTURER"))
            .collect(Collectors.toList());
    }

    @Override
    public List<StudentAssignmentResponse> getStudentAssignments(Long studentId) {
        // Get student's course sessions
        List<CourseSessionDto> courseSessions = courseServiceClient.getCourseSessionByStudentId(studentId);
        List<Long> courseSessionIds = courseSessions.stream()
            .map(CourseSessionDto::getId)
            .collect(Collectors.toList());
        
        if (courseSessionIds.isEmpty()) {
            return List.of();
        }
        
        // Get published assignments for these course sessions
        List<Assignment> assignments = assignmentRepository.findByCourseSessionIdsAndStatus(
            courseSessionIds, Assignment.AssignmentStatus.PUBLISHED);
        
        // Get student's submissions for these assignments
        List<Long> assignmentIds = assignments.stream()
            .map(Assignment::getId)
            .collect(Collectors.toList());
        
        List<Submission> submissions = submissionRepository.findByAssignmentIdsAndStudentId(assignmentIds, studentId);
        
        return assignments.stream()
            .map(assignment -> convertToStudentResponse(assignment, submissions, studentId))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentResponse publishAssignment(Long id, Long lecturerId) {
        Assignment assignment = getAssignmentByIdAndLecturer(id, lecturerId);
        assignment.setStatus(Assignment.AssignmentStatus.PUBLISHED);
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment published: {} by lecturer: {}", id, lecturerId);
        return convertToResponse(updatedAssignment, lecturerId, "LECTURER");
    }

    @Override
    @Transactional
    public AssignmentResponse closeAssignment(Long id, Long lecturerId) {
        Assignment assignment = getAssignmentByIdAndLecturer(id, lecturerId);
        assignment.setStatus(Assignment.AssignmentStatus.CLOSED);
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment closed: {} by lecturer: {}", id, lecturerId);
        return convertToResponse(updatedAssignment, lecturerId, "LECTURER");
    }

    @Override
    @Transactional
    public SubmissionResponse submitAssignment(SubmissionCreateRequest request, Long studentId, String studentName) {
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
            .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        if (assignment.getStatus() != Assignment.AssignmentStatus.PUBLISHED) {
            throw new RuntimeException("Assignment is not available for submission");
        }
        
        // Check if student already submitted
        Optional<Submission> existingSubmission = submissionRepository.findByAssignmentIdAndStudentId(
            request.getAssignmentId(), studentId);
        
        if (existingSubmission.isPresent()) {
            throw new RuntimeException("Assignment already submitted");
        }
        
        boolean isLate = LocalDateTime.now().isAfter(assignment.getDueDate());
        
        Submission submission = Submission.builder()
            .assignment(assignment)
            .studentId(studentId)
            .studentName(studentName)
            .content(request.getContent())
            .attachmentIds(request.getAttachmentIds())
            .status(Submission.SubmissionStatus.SUBMITTED)
            .isLate(isLate)
            .maxScore(assignment.getMaxScore())
            .build();
        
        Submission savedSubmission = submissionRepository.save(submission);
        log.info("Assignment submitted: {} by student: {}", request.getAssignmentId(), studentId);
        
        return convertToSubmissionResponse(savedSubmission);
    }

    @Override
    @Transactional
    public SubmissionResponse updateSubmission(Long submissionId, SubmissionCreateRequest request, Long studentId) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        if (!submission.getStudentId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (submission.getStatus() == Submission.SubmissionStatus.GRADED) {
            throw new RuntimeException("Cannot update graded submission");
        }
        
        submission.setContent(request.getContent());
        submission.setAttachmentIds(request.getAttachmentIds());
        submission.setSubmittedAt(LocalDateTime.now());
        
        boolean isLate = LocalDateTime.now().isAfter(submission.getAssignment().getDueDate());
        submission.setLate(isLate);
        
        Submission updatedSubmission = submissionRepository.save(submission);
        log.info("Submission updated: {} by student: {}", submissionId, studentId);
        
        return convertToSubmissionResponse(updatedSubmission);
    }

    @Override
    @Transactional
    public void deleteSubmission(Long submissionId, Long studentId) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        if (!submission.getStudentId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (submission.getStatus() == Submission.SubmissionStatus.GRADED) {
            throw new RuntimeException("Cannot delete graded submission");
        }
        
        submissionRepository.delete(submission);
        log.info("Submission deleted: {} by student: {}", submissionId, studentId);
    }

    @Override
    @Transactional
    public SubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request, Long lecturerId, String lecturerName) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        if (!submission.getAssignment().getLecturerId().equals(lecturerId)) {
            throw new RuntimeException("Access denied");
        }
        
        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(Submission.SubmissionStatus.GRADED);
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(lecturerId);
        
        Submission gradedSubmission = submissionRepository.save(submission);
        log.info("Submission graded: {} by lecturer: {}", submissionId, lecturerId);
        
        return convertToSubmissionResponse(gradedSubmission);
    }

    @Override
    public List<SubmissionResponse> getAssignmentSubmissions(Long assignmentId, Long lecturerId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        if (!assignment.getLecturerId().equals(lecturerId)) {
            throw new RuntimeException("Access denied");
        }
        
        List<Submission> submissions = submissionRepository.findByAssignmentIdOrderBySubmittedAtDesc(assignmentId);
        return submissions.stream()
            .map(this::convertToSubmissionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponse getSubmissionById(Long submissionId, Long userId, String role) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        // Validate access
        if ("STUDENT".equals(role) && !submission.getStudentId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        if ("LECTURER".equals(role) && !submission.getAssignment().getLecturerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToSubmissionResponse(submission);
    }

    @Override
    public List<SubmissionResponse> getMySubmissions(Long studentId) {
        List<Submission> submissions = submissionRepository.findByStudentIdOrderBySubmittedAtDesc(studentId);
        return submissions.stream()
            .map(this::convertToSubmissionResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponse getMySubmissionForAssignment(Long assignmentId, Long studentId) {
        Optional<Submission> submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
        return submission.map(this::convertToSubmissionResponse).orElse(null);
    }

    // Helper methods
    private Assignment getAssignmentByIdAndLecturer(Long id, Long lecturerId) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        if (!assignment.getLecturerId().equals(lecturerId)) {
            throw new RuntimeException("Access denied");
        }
        
        return assignment;
    }

    private void validateLecturerAccess(Long lecturerId, Long courseSessionId) {
        // This should validate that the lecturer has access to the course session
        // Implementation depends on course service integration
        log.debug("Validating lecturer {} access to course session {}", lecturerId, courseSessionId);
    }

    private AssignmentResponse convertToResponse(Assignment assignment, Long userId, String role) {
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setCourseSessionId(assignment.getCourseSessionId());
        response.setLecturerId(assignment.getLecturerId());
        response.setLecturerName(assignment.getLecturerName());
        response.setDueDate(assignment.getDueDate());
        response.setCreatedAt(assignment.getCreatedAt());
        response.setMaxScore(assignment.getMaxScore());
        response.setType(assignment.getType());
        response.setStatus(assignment.getStatus());
        response.setAttachmentIds(assignment.getAttachmentIds());
        response.setOverdue(LocalDateTime.now().isAfter(assignment.getDueDate()));
        
        // Get submission count
        response.setSubmissionCount(submissionRepository.countByAssignmentId(assignment.getId()));
        
        // For students, include their submission
        if ("STUDENT".equals(role)) {
            Optional<Submission> submission = submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), userId);
            submission.ifPresent(s -> response.setMySubmission(convertToSubmissionResponse(s)));
        }
        
        // Load attachment information from resource service
        if (assignment.getAttachmentIds() != null && !assignment.getAttachmentIds().isEmpty()) {
            response.setAttachments(loadResourceInfo(assignment.getAttachmentIds(), userId, role));
        }
        
        return response;
    }

    private StudentAssignmentResponse convertToStudentResponse(Assignment assignment, List<Submission> submissions, Long studentId) {
        StudentAssignmentResponse response = new StudentAssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setMaxScore(assignment.getMaxScore());
        response.setType(assignment.getType());
        response.setOverdue(LocalDateTime.now().isAfter(assignment.getDueDate()));
        
        // Load attachments
        if (assignment.getAttachmentIds() != null && !assignment.getAttachmentIds().isEmpty()) {
            response.setAttachments(loadResourceInfo(assignment.getAttachmentIds(), studentId, "STUDENT"));
        }
        
        // Find student's submission
        Optional<Submission> submission = submissions.stream()
            .filter(s -> s.getAssignment().getId().equals(assignment.getId()))
            .findFirst();
        
        if (submission.isPresent()) {
            Submission s = submission.get();
            response.setSubmissionStatus(s.getStatus());
            response.setSubmittedAt(s.getSubmittedAt());
            response.setScore(s.getScore());
            response.setFeedback(s.getFeedback());
            response.setLate(s.isLate());
            
            if (s.getStatus() == Submission.SubmissionStatus.GRADED) {
                response.setStatusDisplay("graded");
                response.setGradeDisplay(s.getScore() + "/" + s.getMaxScore());
            } else {
                response.setStatusDisplay("submitted");
            }
        } else {
            response.setSubmissionStatus(Submission.SubmissionStatus.PENDING);
            response.setStatusDisplay("pending");
        }
        
        return response;
    }

    private SubmissionResponse convertToSubmissionResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setAssignmentId(submission.getAssignment().getId());
        response.setAssignmentTitle(submission.getAssignment().getTitle());
        response.setStudentId(submission.getStudentId());
        response.setStudentName(submission.getStudentName());
        response.setContent(submission.getContent());
        response.setAttachmentIds(submission.getAttachmentIds());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setStatus(submission.getStatus());
        response.setScore(submission.getScore());
        response.setMaxScore(submission.getMaxScore());
        response.setFeedback(submission.getFeedback());
        response.setLate(submission.isLate());
        response.setGradedAt(submission.getGradedAt());
        response.setGradedBy(submission.getGradedBy());
        
        // Load attachment information
        if (submission.getAttachmentIds() != null && !submission.getAttachmentIds().isEmpty()) {
            response.setAttachments(loadResourceInfo(submission.getAttachmentIds(), submission.getStudentId(), "STUDENT"));
        }
        
        return response;
    }

    private List<AssignmentResponse.ResourceInfo> loadResourceInfo(List<Long> resourceIds, Long userId, String role) {
        return resourceIds.stream()
            .map(resourceId -> {
                try {
                    ResourceDto resource = resourceServiceClient.getResourceById(resourceId, userId.toString(), role);
                    AssignmentResponse.ResourceInfo info = new AssignmentResponse.ResourceInfo();
                    info.setId(resource.getId());
                    info.setTitle(resource.getTitle());
                    info.setFileName(resource.getFileName());
                    info.setDownloadUrl(resource.getDownloadUrl());
                    info.setFileType(resource.getType());
                    info.setFileSize(resource.getFileSize());
                    return info;
                } catch (Exception e) {
                    log.warn("Failed to load resource info for ID: {}", resourceId, e);
                    return null;
                }
            })
            .filter(info -> info != null)
            .collect(Collectors.toList());
    }
}