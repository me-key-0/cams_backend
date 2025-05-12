package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.dto.AssessmentDto;
import com.cams.grade_service.model.Assessment;
import com.cams.grade_service.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService implements com.cams.grade_service.service.AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public AssessmentDto createAssessment(AssessmentDto dto) {
        Assessment assessment = Assessment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(dto.getType())
                .submissionMode((dto.isGroupWork()) ? Assessment.SubmissionMode.GROUP : Assessment.SubmissionMode.INDIVIDUAL)
                .courseSessionId(dto.getCourseSessionId())
                .dueDate(dto.getDeadline())
                .build();
        return toDto(assessmentRepository.save(assessment));
    }

    @Override
    public AssessmentDto updateAssessment(Long id, AssessmentDto dto) {
        Assessment assessment = assessmentRepository.findById(id).orElseThrow();
        assessment.setTitle(dto.getTitle());
        assessment.setDescription(dto.getDescription());
        assessment.setType(dto.getType());
        assessment.setSubmissionMode((dto.isGroupWork()) ? Assessment.SubmissionMode.GROUP : Assessment.SubmissionMode.INDIVIDUAL);
        assessment.setCourseSessionId(dto.getCourseSessionId());
        assessment.setDueDate(dto.getDeadline());
        return toDto(assessmentRepository.save(assessment));
    }

    @Override
    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }

    @Override
    public AssessmentDto getAssessment(Long id) {
        return toDto(assessmentRepository.findById(id).orElseThrow());
    }

    @Override
    public List<AssessmentDto> getAllAssessments() {
        return assessmentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AssessmentDto> getAssessmentByCourseId(Long courseId) {
        return assessmentRepository.findByCourseSessionId(courseId);
    }

    private AssessmentDto toDto(Assessment assessment) {
        return AssessmentDto.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .type(assessment.getType())
                .isGroupWork(assessment.getSubmissionMode() == Assessment.SubmissionMode.GROUP)
                .courseSessionId(assessment.getCourseSessionId())
                .deadline(assessment.getDueDate())
                .build();
    }
}