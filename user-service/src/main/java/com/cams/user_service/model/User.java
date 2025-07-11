package com.cams.user_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;

    @Setter
    private String username;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    private String profileImage;
    private boolean isVerified;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Student student;

    @OneToOne(mappedBy = "user")
    private Lecturer lecturer;

    @OneToOne(mappedBy = "user")
    private Admin admin;

    @OneToOne(mappedBy = "user")
    private SuperAdmin superAdmin;

    @OneToMany(mappedBy = "user")
    private List<UserSession> sessions;

}
