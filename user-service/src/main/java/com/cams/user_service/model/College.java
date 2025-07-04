package com.cams.user_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "college")
    private List<Department> departments;

    @OneToMany(mappedBy = "college")
    private List<Student> students;

    @OneToMany(mappedBy = "college")
    private List<Lecturer> lecturers;

    @OneToMany(mappedBy = "college")
    private List<Admin> admins;

    public List<Department> getDepartments() {
        return departments;
    }
}