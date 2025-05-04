package com.cams.user_service.service_impl;

import com.cams.user_service.model.SuperAdmin;
import com.cams.user_service.repository.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuperAdminService implements com.cams.user_service.service.SuperAdminService {

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Override
    public Optional<SuperAdmin> getSuperAdminById(Long id) {
        return superAdminRepository.findByUserId(id);
    }

    @Override
    public List<SuperAdmin> getAllSuperAdmins() {
        return superAdminRepository.findAll();
    }
}
