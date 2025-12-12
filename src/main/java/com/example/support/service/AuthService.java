package com.example.support.service;

import com.example.support.model.User;
import com.example.support.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreateUser(String username, String password, String role) {
        User user = userRepository.findById(username).orElse(null);
        if (user == null) {
            user = new User(username, username, password, role);
            userRepository.save(user);
        }
        return user;
    }

    public boolean validateUser(String username, String password) {
        final User user = userRepository.findById(username).orElse(null);
        return user != null && user.getPassword().equals(password);
    }
}