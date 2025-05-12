package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.dto.AssessmentGradeDto;
import com.cams.grade_service.model.AssessmentGrade;
import com.cams.grade_service.model.GradeType;
import com.cams.grade_service.repository.AssessmentGradeRepository;
import com.cams.grade_service.repository.GradeTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssessmentGradeService implements com.cams.grade_service.service.AssessmentGradeService {
    private AssessmentGradeRepository gradeRepo;
    private GradeTypeRepository gradeTypeRepo;

    @Override
    public AssessmentGrade createAssessmentGrade(AssessmentGradeDto dto) {
        Optional<GradeType> gradeType = gradeTypeRepo.findById(dto.getGradeTypeId());

        AssessmentGrade grade = new AssessmentGrade();
        grade.setStudentId(dto.getStudentId());
        grade.setCourseSessionId(dto.getCourseId());
        grade.setGradeType(gradeType.get());
        grade.setTotal(dto.getTotal());
        grade.setGradeValue(dto.getGradeValue());

        return gradeRepo.save(grade);
    }

    @Override
    public List<AssessmentGrade> getGradesForStudentInCourse(Long studentId, Long courseSessionId) {
        return gradeRepo.findByStudentIdAndCourseSessionId(studentId, courseSessionId);
    }
}