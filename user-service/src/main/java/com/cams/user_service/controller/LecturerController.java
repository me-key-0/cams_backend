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
import com.cams.user_service.service.LecturerService;
import com.cams.user_service.dto.LecturerDto;

@RestController
@RequestMapping("/api/users/lecturer")
public class LecturerController {
    @Autowired
    private LecturerService lecturerService;

    private LecturerDto convertToDto(Lecturer lecturer) {
        LecturerDto dto = new LecturerDto();
        dto.setId(lecturer.getId());
        dto.setFirstName(lecturer.getUser().getFirstname());
        dto.setLastName(lecturer.getUser().getLastname());
        dto.setEmail(lecturer.getUser().getEmail());
        dto.setDepartment(lecturer.getDepartment() != null ? lecturer.getDepartment().getName() : null);
        return dto;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<LecturerDto> getLecturerByUserId(@PathVariable Long userId) {
        Optional<Lecturer> lecturer = lecturerService.getLecturerByUserId(userId);
        if (!lecturer.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(convertToDto(lecturer.get()), HttpStatus.OK);
    }


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
