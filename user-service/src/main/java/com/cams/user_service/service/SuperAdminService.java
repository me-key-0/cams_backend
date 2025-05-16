package com.cams.user_service.service;

import com.cams.user_service.model.SuperAdmin;

import java.util.List;
import java.util.Optional;

public interface SuperAdminService {
    Optional<SuperAdmin> getSuperAdminById(Long id);
    List<SuperAdmin> getAllSuperAdmins();
    Boolean isSuperAdmin(Long id);
}
