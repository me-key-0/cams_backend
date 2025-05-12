package com.cams.grade_service.controller;

import com.cams.grade_service.dto.GradeTypeDto;
import com.cams.grade_service.model.GradeType;
import com.cams.grade_service.service.GradeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade-types")
@RequiredArgsConstructor
public class GradeTypeController {

    private final GradeTypeService gradeTypeService;

    @PostMapping
    public ResponseEntity<GradeType> createGradeType(@RequestBody GradeTypeDto dto) {
        return new ResponseEntity<>(gradeTypeService.createGradeType(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GradeType>> getAllGradeTypes() {
        return ResponseEntity.ok(gradeTypeService.getAllGradeTypes());
    }
}