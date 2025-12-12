package com.example.support.controller;

import com.example.support.model.User;
import com.example.support.repository.UserRepository;
import com.example.support.security.JwtTokenProvider;

public class AuthController {
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // Logic login đơn giản: Nếu user chưa có thì tạo mới luôn để test
        User user = userRepository.findById(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            user = new User(loginRequest.getUsername(), loginRequest.getUsername(), loginRequest.getPassword(), loginRequest.getRole());
            userRepository.save(user);
        }

        final String token = tokenProvider.generateToken(user.getUsername(), user.getRole());
        
        return new AuthResponse(token, user.getRole(), user.getId(), user.getUsername());
    }

    public static class LoginRequest {
        private String username, password, role;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class AuthResponse {
        private final String token, role, userId, username;
        
        public AuthResponse(String token, String role, String userId, String username) {
            this.token = token; 
            this.role = role; 
            this.userId = userId; 
            this.username = username;
        }
        
        public String getToken() { return token; }
        public String getRole() { return role; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
    }
}