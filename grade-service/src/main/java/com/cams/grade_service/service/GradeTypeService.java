package com.cams.grade_service.service;

import com.cams.grade_service.dto.GradeTypeDto;
import com.cams.grade_service.model.GradeType;

import java.util.List;

public interface GradeTypeService {
    GradeType createGradeType(GradeTypeDto dto);
    List<GradeType> getAllGradeTypes();
}