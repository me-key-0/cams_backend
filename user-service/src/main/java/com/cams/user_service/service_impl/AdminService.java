package com.cams.user_service.service_impl;

import com.cams.user_service.model.Admin;
import com.cams.user_service.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService implements com.cams.user_service.service.AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findByUserId(id);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
}
