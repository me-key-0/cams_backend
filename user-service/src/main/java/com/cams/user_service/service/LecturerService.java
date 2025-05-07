package com.cams.user_service.service;

import com.cams.user_service.model.Lecturer;

import java.util.List;
import java.util.Optional;

public interface LecturerService {
    Optional<Lecturer> getLecturerById(Long id);
    List<Lecturer> getAllLecturers();
    Boolean isLecturer(Long id);
}
