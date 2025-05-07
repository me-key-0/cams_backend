package com.cams.user_service.service;

import com.cams.user_service.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Optional<Student> getStudentById(Long id);
    List<Student> getAllStudents();
    Boolean isStudent(Long id);
}
