package com.cams.grade_service.service.impl;

import com.cams.grade_service.client.CourseServiceClient;
import com.cams.grade_service.client.ResourceServiceClient;
import com.cams.grade_service.dto.*;
import com.cams.grade_service.model.*;
import com.cams.grade_service.repository.*;
import com.cams.grade_service.service.GradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradingServiceImpl implements GradingService {

    private final GradeTypeRepository gradeTypeRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseServiceClient courseServiceClient;
    private final ResourceServiceClient resourceServiceClient;

    @Override
    @Transactional
    public GradeTypeResponse createGradeType(GradeTypeRequest request, Long lecturerId, String lecturerName) {
        // Validate course session access
        validateLecturerAccess(lecturerId, request.getCourseSessionId());
        
        GradeType gradeType = GradeType.builder()
            .name(request.getName())
            .description(request.getDescription())
            .maxScore(request.getMaxScore())
            .weightPercentage(request.getWeightPercentage())
            .courseSessionId(request.getCourseSessionId())
            .createdBy(lecturerId)
            .category(request.getCategory())
            .isDefault(false)
            .isActive(true)
            .assignmentId(request.getAssignmentId())
            .build();
        
        GradeType savedGradeType = gradeTypeRepository.save(gradeType);
        log.info("Grade type created: {} by lecturer: {}", savedGradeType.getId(), lecturerId);
        
        return convertToGradeTypeResponse(savedGradeType);
    }

    @Override
    @Transactional
    public GradeTypeResponse updateGradeType(Long id, GradeTypeRequest request, Long lecturerId) {
        GradeType gradeType = gradeTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Grade type not found"));
        
        if (!gradeType.getCreatedBy().equals(lecturerId)) {
            throw new RuntimeException("Access denied");
        }
        
        gradeType.setName(request.getName());
        gradeType.setDescription(request.getDescription());
        gradeType.setMaxScore(request.getMaxScore());
        gradeType.setWeightPercentage(request.getWeightPercentage());
        gradeType.setCategory(request.getCategory());
        
        GradeType updatedGradeType = gradeTypeRepository.save(gradeType);
        return convertToGradeTypeResponse(updatedGradeType);
    }

    @Override
    @Transactional
    public void deleteGradeType(Long id, Long lecturerId) {
        GradeType gradeType = gradeTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Grade type not found"));
        
        if (!gradeType.getCreatedBy().equals(lecturerId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (gradeType.getIsDefault()) {
            throw new RuntimeException("Cannot delete default grade type");
        }
        
        gradeType.setIsActive(false);
        gradeTypeRepository.save(gradeType);
    }

    @Override
    public List<GradeTypeResponse> getGradeTypesByCourseSession(Long courseSessionId) {
        List<GradeType> gradeTypes = gradeTypeRepository.findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtAsc(courseSessionId);
        return gradeTypes.stream()
            .map(this::convertToGradeTypeResponse)
            .collect(Collectors.toList());
    }

    @Override
    public GradeTypeResponse getGradeTypeById(Long id) {
        GradeType gradeType = gradeTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Grade type not found"));
        return convertToGradeTypeResponse(gradeType);
    }

    @Override
    @Transactional
    public StudentGradeResponse addOrUpdateGrade(StudentGradeRequest request, Long lecturerId, String lecturerName) {
        GradeType gradeType = gradeTypeRepository.findById(request.getGradeTypeId())
            .orElseThrow(() -> new RuntimeException("Grade type not found"));
        
        validateLecturerAccess(lecturerId, gradeType.getCourseSessionId());
        
        // Check if grade already exists
        Optional<StudentGrade> existingGrade = studentGradeRepository.findByStudentIdAndGradeTypeId(
            request.getStudentId(), request.getGradeTypeId());
        
        StudentGrade grade;
        if (existingGrade.isPresent()) {
            grade = existingGrade.get();
            grade.setScore(request.getScore());
            grade.setFeedback(request.getFeedback());
            grade.setGradedBy(lecturerId);
            grade.setGraderName(lecturerName);
            grade.setGradedAt(LocalDateTime.now());
        } else {
            grade = StudentGrade.builder()
                .studentId(request.getStudentId())
                .studentName("Student " + request.getStudentId()) // Should fetch from user service
                .gradeType(gradeType)
                .score(request.getScore())
                .feedback(request.getFeedback())
                .gradedBy(lecturerId)
                .graderName(lecturerName)
                .groupId(request.getGroupId())
                .build();
        }
        
        StudentGrade savedGrade = studentGradeRepository.save(grade);
        return convertToStudentGradeResponse(savedGrade);
    }

    @Override
    @Transactional
    public List<StudentGradeResponse> addBulkGrades(BulkGradeRequest request, Long lecturerId, String lecturerName) {
        List<StudentGradeResponse> responses = new ArrayList<>();
        
        for (StudentGradeRequest gradeRequest : request.getGrades()) {
            gradeRequest.setGradeTypeId(request.getGradeTypeId());
            if (gradeRequest.getFeedback() == null && request.getFeedback() != null) {
                gradeRequest.setFeedback(request.getFeedback());
            }
            
            StudentGradeResponse response = addOrUpdateGrade(gradeRequest, lecturerId, lecturerName);
            responses.add(response);
        }
        
        return responses;
    }

    @Override
    @Transactional
    public void deleteGrade(Long studentId, Long gradeTypeId, Long lecturerId) {
        GradeType gradeType = gradeTypeRepository.findById(gradeTypeId)
            .orElseThrow(() -> new RuntimeException("Grade type not found"));
        
        validateLecturerAccess(lecturerId, gradeType.getCourseSessionId());
        
        studentGradeRepository.deleteByStudentIdAndGradeTypeId(studentId, gradeTypeId);
    }

    @Override
    public GradebookResponse getGradebook(Long courseSessionId, Long lecturerId) {
        validateLecturerAccess(lecturerId, courseSessionId);
        
        // Get grade types
        List<GradeType> gradeTypes = gradeTypeRepository.findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtAsc(courseSessionId);
        
        // Get all grades for this course session
        List<StudentGrade> allGrades = studentGradeRepository.findByGradeType_CourseSessionIdOrderByStudentNameAsc(courseSessionId);
        
        // Group grades by student
        Map<Long, List<StudentGrade>> gradesByStudent = allGrades.stream()
            .collect(Collectors.groupingBy(StudentGrade::getStudentId));
        
        // Get unique students
        Set<Long> studentIds = gradesByStudent.keySet();
        
        // Build student rows
        List<StudentGradebookRow> studentRows = new ArrayList<>();
        for (Long studentId : studentIds) {
            StudentGradebookRow row = new StudentGradebookRow();
            row.setStudentId(studentId);
            row.setStudentName("Student " + studentId); // Should fetch from user service
            row.setStudentEmail("student" + studentId + "@example.com"); // Should fetch from user service
            
            Map<Long, StudentGradeResponse> gradeMap = new HashMap<>();
            List<StudentGrade> studentGrades = gradesByStudent.get(studentId);
            
            double totalScore = 0.0;
            double totalWeight = 0.0;
            
            for (StudentGrade grade : studentGrades) {
                gradeMap.put(grade.getGradeType().getId(), convertToStudentGradeResponse(grade));
                
                // Calculate weighted score
                double percentage = (grade.getScore() / grade.getGradeType().getMaxScore()) * 100;
                totalScore += percentage * (grade.getGradeType().getWeightPercentage() / 100);
                totalWeight += grade.getGradeType().getWeightPercentage();
            }
            
            row.setGrades(gradeMap);
            row.setFinalGrade(totalWeight > 0 ? totalScore : 0.0);
            row.setLetterGrade(calculateLetterGrade(row.getFinalGrade()));
            
            studentRows.add(row);
        }
        
        // Calculate class averages
        Map<String, Double> classAverages = calculateClassAverages(gradeTypes, allGrades);
        
        GradebookResponse response = new GradebookResponse();
        response.setCourseSessionId(courseSessionId);
        response.setCourseCode("CS101"); // Should fetch from course service
        response.setCourseName("Programming Fundamentals"); // Should fetch from course service
        response.setGradeTypes(gradeTypes.stream().map(this::convertToGradeTypeResponse).collect(Collectors.toList()));
        response.setStudents(studentRows);
        response.setClassAverages(classAverages);
        
        return response;
    }

    @Override
    public byte[] exportGradebookToExcel(Long courseSessionId, Long lecturerId) {
        GradebookResponse gradebook = getGradebook(courseSessionId, lecturerId);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Gradebook");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student ID");
            headerRow.createCell(1).setCellValue("Student Name");
            
            int colIndex = 2;
            for (GradeTypeResponse gradeType : gradebook.getGradeTypes()) {
                headerRow.createCell(colIndex++).setCellValue(gradeType.getName());
            }
            headerRow.createCell(colIndex++).setCellValue("Final Grade");
            headerRow.createCell(colIndex).setCellValue("Letter Grade");
            
            // Create data rows
            int rowIndex = 1;
            for (StudentGradebookRow student : gradebook.getStudents()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getStudentName());
                
                colIndex = 2;
                for (GradeTypeResponse gradeType : gradebook.getGradeTypes()) {
                    StudentGradeResponse grade = student.getGrades().get(gradeType.getId());
                    if (grade != null) {
                        row.createCell(colIndex).setCellValue(grade.getScore());
                    }
                    colIndex++;
                }
                row.createCell(colIndex++).setCellValue(student.getFinalGrade());
                row.createCell(colIndex).setCellValue(student.getLetterGrade());
            }
            
            // Auto-size columns
            for (int i = 0; i <= colIndex; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to export gradebook to Excel", e);
        }
    }

    @Override
    @Transactional
    public List<StudentGradeResponse> importGradesFromExcel(MultipartFile file, Long courseSessionId, Long gradeTypeId, Long lecturerId, String lecturerName) {
        validateLecturerAccess(lecturerId, courseSessionId);
        
        List<StudentGradeResponse> responses = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Assume first row is header, find student name and score columns
            Row headerRow = sheet.getRow(0);
            int nameColumn = -1;
            int scoreColumn = -1;
            
            for (Cell cell : headerRow) {
                String value = cell.getStringCellValue().toLowerCase();
                if (value.contains("name")) {
                    nameColumn = cell.getColumnIndex();
                } else if (value.contains("score") || value.contains("grade")) {
                    scoreColumn = cell.getColumnIndex();
                }
            }
            
            if (nameColumn == -1 || scoreColumn == -1) {
                throw new RuntimeException("Could not find required columns (name and score)");
            }
            
            // Process data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Cell nameCell = row.getCell(nameColumn);
                Cell scoreCell = row.getCell(scoreColumn);
                
                if (nameCell != null && scoreCell != null) {
                    String studentName = nameCell.getStringCellValue();
                    double score = scoreCell.getNumericCellValue();
                    
                    // Find student by name (this is simplified - in real implementation, you'd have better matching)
                    Long studentId = findStudentIdByName(studentName, courseSessionId);
                    
                    if (studentId != null) {
                        StudentGradeRequest request = new StudentGradeRequest();
                        request.setStudentId(studentId);
                        request.setGradeTypeId(gradeTypeId);
                        request.setScore(score);
                        
                        StudentGradeResponse response = addOrUpdateGrade(request, lecturerId, lecturerName);
                        responses.add(response);
                    }
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to import grades from Excel", e);
        }
        
        return responses;
    }

    @Override
    public StudentAssessmentOverviewResponse getStudentAssessmentOverview(Long studentId, Long courseSessionId) {
        List<AssessmentItemResponse> assessments = getStudentAssessments(studentId, courseSessionId);
        
        int totalAssessments = assessments.size();
        int completedAssessments = (int) assessments.stream().filter(a -> "graded".equals(a.getStatus())).count();
        int pendingAssessments = totalAssessments - completedAssessments;
        
        // Calculate overall grade based on weighted grade types
        List<GradeType> gradeTypes = gradeTypeRepository.findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtAsc(courseSessionId);
        List<StudentGrade> studentGrades = studentGradeRepository.findByStudentIdAndGradeType_CourseSessionId(studentId, courseSessionId);
        
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (GradeType gradeType : gradeTypes) {
            Optional<StudentGrade> gradeOpt = studentGrades.stream()
                .filter(g -> g.getGradeType().getId().equals(gradeType.getId()))
                .findFirst();
            
            if (gradeOpt.isPresent()) {
                StudentGrade grade = gradeOpt.get();
                double percentage = (grade.getScore() / gradeType.getMaxScore()) * 100;
                totalWeightedScore += percentage * (gradeType.getWeightPercentage() / 100);
                totalWeight += gradeType.getWeightPercentage();
            }
        }
        
        double overallGrade = totalWeight > 0 ? totalWeightedScore : 0.0;
        
        StudentAssessmentOverviewResponse response = new StudentAssessmentOverviewResponse();
        response.setCourseSessionId(courseSessionId);
        response.setCourseCode("CS101"); // Should fetch from course service
        response.setCourseName("Programming Fundamentals"); // Should fetch from course service
        response.setAssessments(assessments);
        response.setTotalAssessments(totalAssessments);
        response.setCompletedAssessments(completedAssessments);
        response.setPendingAssessments(pendingAssessments);
        response.setOverallGrade(overallGrade);
        response.setOverallLetterGrade(calculateLetterGrade(overallGrade));
        
        return response;
    }

    @Override
    public List<AssessmentItemResponse> getStudentAssessments(Long studentId, Long courseSessionId) {
        List<AssessmentItemResponse> assessments = new ArrayList<>();
        
        // Get all grade types for this course session
        List<GradeType> gradeTypes = gradeTypeRepository.findByCourseSessionIdAndIsActiveTrueOrderByCreatedAtAsc(courseSessionId);
        
        // Get student's grades for all grade types
        List<StudentGrade> studentGrades = studentGradeRepository.findByStudentIdAndGradeType_CourseSessionId(studentId, courseSessionId);
        Map<Long, StudentGrade> gradesByTypeId = studentGrades.stream()
            .collect(Collectors.toMap(g -> g.getGradeType().getId(), g -> g));
        
        // Process each grade type
        for (GradeType gradeType : gradeTypes) {
            AssessmentItemResponse item = new AssessmentItemResponse();
            item.setId(gradeType.getId());
            item.setTitle(gradeType.getName());
            item.setDescription(gradeType.getDescription());
            item.setType(gradeType.getCategory().name());
            item.setMaxScore(gradeType.getMaxScore());
            
            // Check if this is an assignment-based grade type
            if (gradeType.getAssignmentId() != null) {
                // Handle assignment-based grade type
                Optional<Assignment> assignmentOpt = assignmentRepository.findById(gradeType.getAssignmentId());
                if (assignmentOpt.isPresent()) {
                    Assignment assignment = assignmentOpt.get();
                    item.setTitle(assignment.getTitle());
                    item.setDescription(assignment.getDescription());
                    item.setType("ASSIGNMENT");
                    item.setDueDate(assignment.getDueDate());
                    item.setIsOverdue(LocalDateTime.now().isAfter(assignment.getDueDate()));
                    
                    // Check submission status
                    Optional<Submission> submission = submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), studentId);
                    if (submission.isPresent()) {
                        Submission sub = submission.get();
                        item.setStatus(sub.getStatus().name().toLowerCase());
                        item.setScore(sub.getScore());
                        item.setFeedback(sub.getFeedback());
                        item.setIsLate(sub.isLate());
                        
                        if (sub.getScore() != null) {
                            item.setGradeDisplay(sub.getScore() + "/" + assignment.getMaxScore());
                        } else {
                            item.setGradeDisplay("submitted");
                        }
                    } else {
                        item.setStatus("pending");
                        item.setGradeDisplay("pending");
                    }
                    
                    // Load attachments
                    if (assignment.getAttachmentIds() != null && !assignment.getAttachmentIds().isEmpty()) {
                        item.setAttachments(loadAttachmentInfo(assignment.getAttachmentIds()));
                    }
                }
            } else {
                // Handle non-assignment grade types (exams, quizzes, participation, etc.)
                StudentGrade grade = gradesByTypeId.get(gradeType.getId());
                
                if (grade != null) {
                    // Student has a grade for this type
                    item.setStatus("graded");
                    item.setScore(grade.getScore());
                    item.setFeedback(grade.getFeedback());
                    item.setGradeDisplay(grade.getScore() + "/" + gradeType.getMaxScore());
                } else {
                    // Student doesn't have a grade yet
                    item.setStatus("pending");
                    item.setGradeDisplay("pending");
                }
                
                item.setIsLate(false);
                item.setIsOverdue(false);
            }
            
            assessments.add(item);
        }
        
        // Sort assessments: assignments by due date (most recent first), then others by creation date
        return assessments.stream()
            .sorted((a, b) -> {
                // Assignments with due dates first, sorted by due date (most recent first)
                if (a.getDueDate() != null && b.getDueDate() != null) {
                    return b.getDueDate().compareTo(a.getDueDate());
                } else if (a.getDueDate() != null) {
                    return -1; // a has due date, b doesn't - a comes first
                } else if (b.getDueDate() != null) {
                    return 1; // b has due date, a doesn't - b comes first
                } else {
                    // Neither has due date, sort by title
                    return a.getTitle().compareTo(b.getTitle());
                }
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createDefaultGradeTypes(Long courseSessionId, Long lecturerId, String lecturerName) {
        // Check if default grade types already exist
        List<GradeType> existingDefaults = gradeTypeRepository.findByCourseSessionIdAndIsDefaultTrueAndIsActiveTrueOrderByCreatedAtAsc(courseSessionId);
        if (!existingDefaults.isEmpty()) {
            return; // Default grade types already exist
        }
        
        // Create default grade types
        List<GradeType> defaultGradeTypes = Arrays.asList(
            GradeType.builder()
                .name("Midterm Exam")
                .description("Midterm examination")
                .maxScore(100)
                .weightPercentage(30.0)
                .courseSessionId(courseSessionId)
                .createdBy(lecturerId)
                .category(GradeType.GradeTypeCategory.EXAM)
                .isDefault(true)
                .isActive(true)
                .build(),
            GradeType.builder()
                .name("Final Exam")
                .description("Final examination")
                .maxScore(100)
                .weightPercentage(40.0)
                .courseSessionId(courseSessionId)
                .createdBy(lecturerId)
                .category(GradeType.GradeTypeCategory.EXAM)
                .isDefault(true)
                .isActive(true)
                .build(),
            GradeType.builder()
                .name("Assignments")
                .description("Course assignments")
                .maxScore(100)
                .weightPercentage(20.0)
                .courseSessionId(courseSessionId)
                .createdBy(lecturerId)
                .category(GradeType.GradeTypeCategory.ASSIGNMENT)
                .isDefault(true)
                .isActive(true)
                .build(),
            GradeType.builder()
                .name("Participation")
                .description("Class participation")
                .maxScore(100)
                .weightPercentage(10.0)
                .courseSessionId(courseSessionId)
                .createdBy(lecturerId)
                .category(GradeType.GradeTypeCategory.PARTICIPATION)
                .isDefault(true)
                .isActive(true)
                .build()
        );
        
        gradeTypeRepository.saveAll(defaultGradeTypes);
        log.info("Created default grade types for course session: {}", courseSessionId);
    }

    // Helper methods
    private GradeTypeResponse convertToGradeTypeResponse(GradeType gradeType) {
        GradeTypeResponse response = new GradeTypeResponse();
        response.setId(gradeType.getId());
        response.setName(gradeType.getName());
        response.setDescription(gradeType.getDescription());
        response.setMaxScore(gradeType.getMaxScore());
        response.setWeightPercentage(gradeType.getWeightPercentage());
        response.setCourseSessionId(gradeType.getCourseSessionId());
        response.setCreatedBy(gradeType.getCreatedBy());
        response.setCreatedAt(gradeType.getCreatedAt());
        response.setCategory(gradeType.getCategory());
        response.setIsDefault(gradeType.getIsDefault());
        response.setIsActive(gradeType.getIsActive());
        response.setAssignmentId(gradeType.getAssignmentId());
        
        // Load assignment title if exists
        if (gradeType.getAssignmentId() != null) {
            assignmentRepository.findById(gradeType.getAssignmentId())
                .ifPresent(assignment -> response.setAssignmentTitle(assignment.getTitle()));
        }
        
        return response;
    }

    private StudentGradeResponse convertToStudentGradeResponse(StudentGrade grade) {
        StudentGradeResponse response = new StudentGradeResponse();
        response.setId(grade.getId());
        response.setStudentId(grade.getStudentId());
        response.setStudentName(grade.getStudentName());
        response.setGradeTypeId(grade.getGradeType().getId());
        response.setGradeTypeName(grade.getGradeType().getName());
        response.setMaxScore(grade.getGradeType().getMaxScore());
        response.setScore(grade.getScore());
        response.setFeedback(grade.getFeedback());
        response.setGradedBy(grade.getGradedBy());
        response.setGraderName(grade.getGraderName());
        response.setGradedAt(grade.getGradedAt());
        response.setGroupId(grade.getGroupId());
        
        return response;
    }

    private void validateLecturerAccess(Long lecturerId, Long courseSessionId) {
        // This should validate that the lecturer has access to the course session
        // Implementation depends on course service integration
        log.debug("Validating lecturer {} access to course session {}", lecturerId, courseSessionId);
    }

    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }

    private Map<String, Double> calculateClassAverages(List<GradeType> gradeTypes, List<StudentGrade> allGrades) {
        Map<String, Double> averages = new HashMap<>();
        
        for (GradeType gradeType : gradeTypes) {
            List<StudentGrade> gradesForType = allGrades.stream()
                .filter(g -> g.getGradeType().getId().equals(gradeType.getId()))
                .collect(Collectors.toList());
            
            if (!gradesForType.isEmpty()) {
                double average = gradesForType.stream()
                    .mapToDouble(StudentGrade::getScore)
                    .average()
                    .orElse(0.0);
                averages.put(gradeType.getName(), average);
            }
        }
        
        return averages;
    }

    private Long findStudentIdByName(String studentName, Long courseSessionId) {
        // This is a simplified implementation
        // In a real system, you would query the user service or have a proper student lookup
        return 1L; // Placeholder
    }

    private List<AssessmentItemResponse.AttachmentInfo> loadAttachmentInfo(List<Long> attachmentIds) {
        // This would load attachment information from resource service
        return new ArrayList<>(); // Placeholder
    }
}