package com.example.support.service;

import com.example.support.model.User;
import com.example.support.repository.UserRepository;
import java.util.List;

public class RoomService {
    private final UserRepository userRepository;

    public RoomService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getCustomers() {
        return userRepository.findByRole("CUSTOMER");
    }

    public List<User> getManagers() {
        return userRepository.findByRole("MANAGER");
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}