package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.dto.GradeTypeDto;
import com.cams.grade_service.model.GradeType;
import com.cams.grade_service.repository.GradeTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeTypeService implements com.cams.grade_service.service.GradeTypeService {

    @Autowired
    private GradeTypeRepository typeRepo;

    @Override
    public GradeType createGradeType(GradeTypeDto dto) {
        // GradeType type = new GradeType(dto.getName());
        // return typeRepo.save(type);
        return null;
    }

    @Override
    public List<GradeType> getAllGradeTypes() {
        return typeRepo.findAll();
    }
}
