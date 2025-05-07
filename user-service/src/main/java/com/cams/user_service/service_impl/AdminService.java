package com.cams.user_service.service_impl;

import com.cams.user_service.model.Admin;
import com.cams.user_service.model.User;
import com.cams.user_service.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdminService implements com.cams.user_service.service.AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserService userService;

    @Override
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findByUserId(id);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Boolean isAdmin(Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()){
            String role = user.get().getRole();
            return Objects.equals(role, "ADMIN");
        }
        return false;
    }
}
