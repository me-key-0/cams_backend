package com.cams.user_service.service;

import com.cams.user_service.model.Semester;

import java.util.List;
import java.util.Optional;

public interface SemesterService {
    Optional<Semester> getSemesterById(Long id);
    List<Semester> getAllSemesters();
}
