package com.cams.user_service.service_impl;

import com.cams.user_service.model.Student;
import com.cams.user_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements com.cams.user_service.service.StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findByUserId(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
