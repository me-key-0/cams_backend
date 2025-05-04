package com.cams.user_service.service;

import com.cams.user_service.model.College;

import java.util.List;
import java.util.Optional;

public interface CollegeService {
    Optional<College> getCollegeById(Long id);
    List<College> getAllColleges();
}
