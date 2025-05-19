package com.cams.auth_service.service.impl;

import com.cams.auth_service.client.UserResponse;
import com.cams.auth_service.client.UserServiceClient;
import com.cams.auth_service.dto.AuthResponse;
import com.cams.auth_service.dto.LoginRequest;
import com.cams.auth_service.dto.UserDto;
import com.cams.auth_service.model.Role;
import com.cams.auth_service.security.JwtTokenProvider;
import com.cams.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Validate credentials with user service
        boolean isValid = userServiceClient.validateCredentials(loginRequest.getEmail(), loginRequest.getPassword());
        if (!isValid) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Get user details
        UserResponse userResponse = userServiceClient.getUserByEmail(loginRequest.getEmail());
        if (!userResponse.isVerified()) {
            throw new BadCredentialsException("Account is not verified");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
            userResponse.getId(),
            userResponse.getEmail(),
            userResponse.getRole()
        );

        // Create user DTO
        UserDto userDto = UserDto.builder()
            .id(userResponse.getId())
            .email(userResponse.getEmail())
            .firstname(userResponse.getFirstname())
            .lastname(userResponse.getLastname())
            .role(Role.valueOf(userResponse.getRole()))
            .profileImage(userResponse.getProfileImage())
            .build();

        // Return authentication response
        return AuthResponse.builder()
            .token(token)
            .user(userDto)
            .build();
    }
} 