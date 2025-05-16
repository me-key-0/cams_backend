package com.cams.course_service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cams.course_service.dto.CourseDto;
import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.model.CourseSession;
import com.cams.course_service.repository.CourseSessionRepository;

@Service
public class CourseSessionService implements com.cams.course_service.service.CourseSessionService {
    
    @Autowired
    private CourseSessionRepository courseSessionRepository;

    @Override
    public CourseSession getCourseSession(Long id) {
        
        return courseSessionRepository.findById(id).get();
    }


    public List<CourseSessionDto> getCourseSessionsByStudent(Long studentId) {
        List<CourseSession> sessions = courseSessionRepository.findByStudentId(studentId);
        return sessions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private CourseSessionDto mapToDTO(CourseSession cs) {
        CourseDto courseDTO = CourseDto.builder()
            .code(cs.getCourse().getCode())
            .name(cs.getCourse().getName())
            .creditHour(cs.getCourse().getCreditHour())
            .build();

        return CourseSessionDto.builder()
            .id(cs.getId())
            .year(cs.getYear())
            .semester(cs.getSemester())
            .academicYear(cs.getAcademicYear())
            .course(courseDTO)
            .build();
    }

    

}
