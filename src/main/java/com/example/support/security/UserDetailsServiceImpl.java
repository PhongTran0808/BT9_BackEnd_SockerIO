package com.example.support.security;

import com.example.support.model.User;
import com.example.support.repository.UserRepository;

public class UserDetailsServiceImpl {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loadUserByUsername(String username) throws RuntimeException {
        return userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}