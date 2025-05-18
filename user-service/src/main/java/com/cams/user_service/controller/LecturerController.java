package com.cams.user_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cams.user_service.model.Lecturer;
import com.cams.user_service.service.LecturerService;
import com.cams.user_service.dto.LecturerDto;

@RestController
@RequestMapping("/api/user/lecturer")
public class LecturerController {
    @Autowired
    private LecturerService lecturerService;

    @GetMapping("/{lecturerId}")
    public ResponseEntity<LecturerDto> getLecturerById(@PathVariable Long lecturerId) {
        System.out.println("Received request for lecturer ID: " + lecturerId);
        
        Optional<Lecturer> lecturer = lecturerService.getLecturerById(lecturerId);
        System.out.println("Lecturer found: " + lecturer.isPresent());
        
        if (!lecturer.isPresent()) {
            System.out.println("No lecturer found with ID: " + lecturerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Lecturer l = lecturer.get();
        System.out.println("Lecturer details - ID: " + l.getId());
        System.out.println("User details - " + (l.getUser() != null ? 
            "FirstName: " + l.getUser().getFirstname() + ", LastName: " + l.getUser().getLastname() :
            "User is null"));
        
        LecturerDto dto = new LecturerDto();
        dto.setId(l.getId());
        dto.setFirstName(l.getUser().getFirstname());
        dto.setLastName(l.getUser().getLastname());
        dto.setEmail(l.getUser().getEmail());
        dto.setDepartment(l.getDepartment() != null ? l.getDepartment().getName() : null);
        
        System.out.println("Returning DTO with name: " + dto.getFullName());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
