package com.cams.user_service.service_impl;

import com.cams.user_service.dto.UserDto;
import com.cams.user_service.dto.UserResponse;
import com.cams.user_service.model.User;
import com.cams.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements com.cams.user_service.service.UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = new User();

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public Void deleteUser(Long id) {
        Optional<User> user = getUserById(id);
        if(user.isPresent()) {
            userRepository.deleteById(id);
        }
        return null;
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        log.info("Validating credentials for email: {}", email);
        UserResponse user = getUserByEmail(email);
        if (user == null) {
            log.warn("User not found for email: {}", email);
            return false;
        }
        
        
        log.info("Found user: {}", user.getEmail());
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        log.info("Password matches: {}", matches);
        return matches;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            log.info("Found user: {}", userOpt.get().getEmail());
        } else {
            log.warn("No user found for email: {}", email);
            return null;
        }
        User user = userOpt.get();
        UserResponse response = new UserResponse(user.getId(), user.getEmail(),user.getPassword(), user.getFirstname(), user.getLastname(), user.getRole(), user.getProfileImage(), user.isVerified());
        
        return response;
    }
}

