package com.cams.user_service.service_impl;

import com.cams.user_service.model.College;
import com.cams.user_service.model.Department;
import com.cams.user_service.repository.DepartmentRepository;
import com.cams.user_service.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService implements com.cams.user_service.service.DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CollegeService collegeService;

    @Override
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Optional<List<Department>> getDepartmentByCollegeId(Long collegeId) {
        Optional<College> college = collegeService.getCollegeById(collegeId);
        if (college.isPresent()) {
            return Optional.ofNullable(college.get().getDepartments());
        }
        return Optional.empty();
    }

}
