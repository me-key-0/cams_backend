package com.cams.auth_service.client;


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
    private String email;
    private String firstname;
    private String lastname;
    private String role;
    private String profileImage;
    private boolean isVerified;
} 