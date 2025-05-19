package com.cams.auth_service.dto;

import com.cams.auth_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private Role role;
    private String profileImage;
} 