package com.cams.user_service.service;

import com.cams.user_service.model.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    Optional<Admin> getAdminById(Long id);
    List<Admin> getAllAdmins();
    Boolean isAdmin(Long id);
}
