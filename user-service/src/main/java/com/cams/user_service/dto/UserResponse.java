package com.cams.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private Long departmentId;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String role;
    private String profileImage;
    private boolean isVerified;
} 