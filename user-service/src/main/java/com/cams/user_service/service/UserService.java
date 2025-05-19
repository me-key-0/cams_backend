package com.cams.user_service.service;

import com.cams.user_service.dto.UserDto;
import com.cams.user_service.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User createUser(UserDto userDto);
    Void deleteUser(Long id);
    boolean validateCredentials(String email, String password);
    Optional<User> getUserByEmail(String email);
}
