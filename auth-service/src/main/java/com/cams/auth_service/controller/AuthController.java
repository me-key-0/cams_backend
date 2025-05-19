package com.cams.auth_service.controller;

import com.cams.auth_service.dto.AuthResponse;
import com.cams.auth_service.dto.LoginRequest;
import com.cams.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.debug("Received login request for email: {}", loginRequest.getEmail());
        try {
            AuthResponse response = authService.login(loginRequest);
            log.debug("Login successful for user: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {} - Error: {}", loginRequest.getEmail(), e.getMessage(), e);
            throw e;
        }
    }
} 