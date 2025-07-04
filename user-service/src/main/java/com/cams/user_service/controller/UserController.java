package com.cams.user_service.controller;

import com.cams.user_service.dto.UserDto;
import com.cams.user_service.dto.UserResponse;
import com.cams.user_service.model.User;
import com.cams.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")  // Updated to match auth-service client path
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        User createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id).get();
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateCredentials(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        boolean isValid = userService.validateCredentials(email, password);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return new ResponseEntity<UserResponse>(user,HttpStatus.OK);
    }
}
