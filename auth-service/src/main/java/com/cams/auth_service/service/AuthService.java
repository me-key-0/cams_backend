package com.cams.auth_service.service;

import com.cams.auth_service.dto.AuthResponse;
import com.cams.auth_service.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
} 