package com.cams.user_service.service;

import com.cams.user_service.dto.StudentDto;
import com.cams.user_service.model.Student;

import java.util.List;

public interface StudentService {
    StudentDto getStudentById(Long id);
    StudentDto getStudentByUserId(Long id);
    List<Student> getAllStudents();
    Boolean isStudent(Long id);
}
