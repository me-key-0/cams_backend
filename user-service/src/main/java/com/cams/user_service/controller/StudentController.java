package com.cams.user_service.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cams.user_service.model.Lecturer;
import com.cams.user_service.model.Student;
import com.cams.user_service.service.LecturerService;
import com.cams.user_service.service.StudentService;
import com.cams.user_service.dto.LecturerDto;
import com.cams.user_service.dto.StudentDto;

@RestController
@RequestMapping("/api/users/student")
public class StudentController {
    @Autowired
    private StudentService studentService;


    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long studentId) {
        System.out.println("Received request for student ID: " + studentId);
        
        StudentDto student = studentService.getStudentById(studentId);
        
        if (student == null) {
            System.out.println("No student found with ID: " + studentId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<StudentDto>(student, HttpStatus.OK);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentDto> getLecturerByUserId(@PathVariable Long userId) {
        StudentDto student = studentService.getStudentByUserId(userId);
        if (student == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<StudentDto>(student, HttpStatus.OK);
    }

}

