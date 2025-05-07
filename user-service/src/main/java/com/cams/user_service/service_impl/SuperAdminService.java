package com.cams.user_service.service_impl;

import com.cams.user_service.model.SuperAdmin;
import com.cams.user_service.model.User;
import com.cams.user_service.repository.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SuperAdminService implements com.cams.user_service.service.SuperAdminService {

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private UserService userService;

    @Override
    public Optional<SuperAdmin> getSuperAdminById(Long id) {
        return superAdminRepository.findByUserId(id);
    }

    @Override
    public List<SuperAdmin> getAllSuperAdmins() {
        return superAdminRepository.findAll();
    }

    @Override
    public Boolean isSuperAdmin(Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()){
            String role = user.get().getRole();
            return Objects.equals(role, "SUPERADMIN");
        }
        return false;
    }
}
