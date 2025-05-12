package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.dto.AssessmentSubmissionDto;
import com.cams.grade_service.model.AssessmentSubmission;
import com.cams.grade_service.repository.AssessmentRepository;
import com.cams.grade_service.repository.AssessmentSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssessmentSubmissionService implements com.cams.grade_service.service.AssessmentSubmissionService {

    @Autowired
    private AssessmentSubmissionRepository submissionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Override
    public AssessmentSubmissionDto submitAssessment(AssessmentSubmissionDto dto) {
        AssessmentSubmission submission = AssessmentSubmission.builder()
                .assessment(assessmentRepository.findById(dto.getAssessmentId()).orElseThrow())
                .studentId(dto.getStudentId())
                .fileUrl(dto.getSubmissionUrl())
                .submittedAt(dto.getSubmittedAt())
                .score(dto.getScore())
                .build();
        return toDto(submissionRepository.save(submission));
    }

    @Override
    public AssessmentSubmissionDto updateSubmission(Long id, AssessmentSubmissionDto dto) {
        AssessmentSubmission submission = submissionRepository.findById(id).orElseThrow();
        submission.setFileUrl(dto.getSubmissionUrl());
        submission.setSubmittedAt(dto.getSubmittedAt());
        submission.setScore(dto.getScore());
        return toDto(submissionRepository.save(submission));
    }

    @Override
    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    @Override
    public AssessmentSubmissionDto getSubmission(Long id) {
        return toDto(submissionRepository.findById(id).orElseThrow());
    }

    @Override
    public List<AssessmentSubmissionDto> getAllSubmissions() {
        return submissionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AssessmentSubmissionDto> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AssessmentSubmissionDto> getSubmissionsByAssessment(Long assessmentId) {
        return submissionRepository.findByAssessmentId(assessmentId).stream().map(this::toDto).collect(Collectors.toList());
    }



    private AssessmentSubmissionDto toDto(AssessmentSubmission submission) {
        return AssessmentSubmissionDto.builder()
                .id(submission.getId())
                .assessmentId(submission.getAssessment().getId())
                .studentId(submission.getStudentId())
                .submissionUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .score(submission.getScore())
                .build();
    }
}
