package com.cams.user_service.service_impl;

import com.cams.user_service.model.Lecturer;
import com.cams.user_service.model.User;
import com.cams.user_service.repository.LecturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LecturerService implements com.cams.user_service.service.LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private UserService userService;

    @Override
    public Optional<Lecturer> getLecturerById(Long id) {
        return lecturerRepository.findById(id);
    }

    @Override
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    @Override
    public Boolean isLecturer(Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()){
            String role = user.get().getRole();
            return Objects.equals(role, "LECTURER");
        }
        return false;
    }
}
