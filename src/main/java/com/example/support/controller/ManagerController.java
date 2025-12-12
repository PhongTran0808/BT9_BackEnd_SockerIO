package com.example.support.controller;

import com.example.support.model.User;
import com.example.support.repository.UserRepository;
import java.util.List;

public class ManagerController {
    private final UserRepository userRepository;

    public ManagerController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getCustomers() {
        return userRepository.findByRole("CUSTOMER");
    }
}