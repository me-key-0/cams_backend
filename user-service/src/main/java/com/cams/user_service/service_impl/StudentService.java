package com.cams.user_service.service_impl;

import com.cams.user_service.model.Student;
import com.cams.user_service.model.User;
import com.cams.user_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService implements com.cams.user_service.service.StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Boolean isStudent(Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()){
            String role = user.get().getRole();
            return Objects.equals(role, "STUDENT");
        }
        return false;
    }
}
