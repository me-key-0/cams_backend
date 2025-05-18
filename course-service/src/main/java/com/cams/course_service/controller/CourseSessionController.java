package com.cams.course_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cams.course_service.dto.CourseDto;
import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.model.Course;
import com.cams.course_service.model.CourseSession;
import com.cams.course_service.serviceImpl.CourseSessionService;
import org.springframework.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/session")
public class CourseSessionController {

    @Autowired
    private CourseSessionService courseSessionService;
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseSessionDto> getCourseSession(@PathVariable Long id) {
        CourseSession session = courseSessionService.getCourseSession(id);
        Course course = session.getCourse();
        CourseDto courseDto = new CourseDto();
        courseDto.setCode(course.getCode());
        courseDto.setCreditHour(course.getCreditHour());
        courseDto.setName(course.getName());

        CourseSessionDto dto = new CourseSessionDto();
        dto.setId(session.getId());
        dto.setAcademicYear(session.getAcademicYear());
        dto.setYear(session.getYear());
        dto.setSemester(session.getSemester());
        dto.setCourse(courseDto);
    
        return new ResponseEntity<CourseSessionDto>(dto, HttpStatus.OK);    
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkCourseSessionExists(@PathVariable Long id) {
        try {
            courseSessionService.getCourseSession(id);
            return ResponseEntity.ok(true);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(false);
        }
    }
}
