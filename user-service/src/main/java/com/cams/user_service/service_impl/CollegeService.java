package com.cams.user_service.service_impl;

import com.cams.user_service.model.College;
import com.cams.user_service.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollegeService implements com.cams.user_service.service.CollegeService {

    @Autowired
    private CollegeRepository collegeRepository;

    @Override
    public Optional<College> getCollegeById(Long id) {
        return collegeRepository.findById(id);
    }

    @Override
    public List<College> getAllColleges() {
        return collegeRepository.findAll();
    }
}
