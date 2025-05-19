package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.client.CourseServiceClient;
import com.cams.grade_service.dto.CourseGradeResponseDto;
import com.cams.grade_service.dto.CourseSessionDto;
import com.cams.grade_service.dto.GradeReportDto;
import com.cams.grade_service.model.GradeReport;
import com.cams.grade_service.repository.GradeReportRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeReportService implements com.cams.grade_service.service.GradeReportService {

    @Autowired
    private GradeReportRepository gradeReportRepository;

    // @Autowired
    // private GradeTypeRepository typeRepo;

    @Autowired
    private CourseServiceClient client;

    @Override
    public GradeReport postFinalGrade(GradeReportDto dto) {

        GradeReport report = new GradeReport(dto.getStudentId(), dto.getCourseId(), dto.getFinalGrade());
        return gradeReportRepository.save(report);
    }

    // @Override
    // public GradeReport getFinalGrade(Long studentId, Long courseId) {
    //     List<GradeType> type = typeRepo.findByCourseSessionId(courseId);
    //     List<String> types = new ArrayList<>();

    //     for (GradeType t : type) {
    //         types.add(t.getName());
    //         System.out.println(t.getName());
    //     }

        
        
    //     return reportRepo.findByStudentIdAndCourseSessionId(studentId, courseId);
    // }

    @Override
    public List<CourseGradeResponseDto> getGradeReports(Long studentId, Integer year, Integer semester) {
        List<CourseSessionDto> sessions = client.getCourseSessionByStudentId(studentId);
        

        // Filter by academic year and semester
        List<Long> sessionIds = sessions.stream()
            .filter(cs -> cs.getYear() == year && cs.getSemester() == semester)
            .map(CourseSessionDto::getId)
            .toList();
        

        List<GradeReport> reports = gradeReportRepository.findByStudentIdAndCourseSessionIds(studentId,sessionIds);
        return reports.stream().map(report -> {
            CourseSessionDto courseSession = sessions.stream().filter(cs -> cs.getId().equals(report.getCourseSessionId()))
                .findFirst().orElse(null);

            return CourseGradeResponseDto.builder()
                .courseCode(courseSession.getCourse().getCode())
                .courseName(courseSession.getCourse().getName())
                .creditHour(courseSession.getCourse().getCreditHour())
                .finalGrade(report.getFinalGrade())
                .build();
        }).toList();
    }

}