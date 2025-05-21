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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.debug("Attempting to validate credentials for user: {}", loginRequest.getEmail());
        
        // Validate credentials with user service
        
        boolean isValid = userServiceClient.validateCredentials(loginRequest.getEmail(), loginRequest.getPassword());
        
        log.debug("Credential validation result for user {}: {}", loginRequest.getEmail(), isValid);
        
        if (!isValid) {
            log.warn("Invalid credentials for user: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        // Get user details
        log.debug("Fetching user details for: {}", loginRequest.getEmail());
        UserResponse userResponse = userServiceClient.getUserByEmail(loginRequest.getEmail());
        log.debug("User details retrieved for: {}, verified: {}", loginRequest.getEmail(), userResponse.isVerified());
        
        if (!userResponse.isVerified()) {
            log.warn("Unverified account attempt for user: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Account is not verified");
        }

        // Generate JWT token
        log.debug("Generating JWT token for user: {}", loginRequest.getEmail());
        String token = jwtTokenProvider.generateToken(
            userResponse.getId(),
            userResponse.getEmail(),
            userResponse.getDepartmentId(),
            userResponse.getRole()
        );
        log.debug("JWT token generated successfully for user: {}", loginRequest.getEmail());

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