package com.cams.grade_service.serviceImpl;

import com.cams.grade_service.dto.GradeTypeDto;
import com.cams.grade_service.model.GradeType;
import com.cams.grade_service.repository.GradeTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeTypeService implements com.cams.grade_service.service.GradeTypeService {
    private GradeTypeRepository typeRepo;

    @Override
    public GradeType createGradeType(GradeTypeDto dto) {
        return typeRepo.save(new GradeType(null, dto.getName()));
    }

    @Override
    public List<GradeType> getAllGradeTypes() {
        return typeRepo.findAll();
    }
}
