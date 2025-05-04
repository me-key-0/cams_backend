package com.cams.user_service.service;

import com.cams.user_service.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Optional<Department> getDepartmentById(Long id);
    List<Department> getAllDepartments();
    Optional<List<Department>> getDepartmentByCollegeId(Long collegeId);
}
