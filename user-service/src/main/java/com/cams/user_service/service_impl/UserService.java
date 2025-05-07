package com.cams.user_service.service_impl;

import com.cams.user_service.dto.UserDto;
import com.cams.user_service.model.User;
import com.cams.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements com.cams.user_service.service.UserService {

    @Autowired
    private UserRepository userRepository;


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
        user.setPassword(userDto.getPassword()); // You should hash the password!

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

}
