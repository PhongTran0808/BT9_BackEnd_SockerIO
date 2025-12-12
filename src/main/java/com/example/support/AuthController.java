package com.example.support;

import java.util.Optional;

public class AuthController {
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        // Tìm user theo username
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Kiểm tra role khớp
            if (request.getRole().equals(user.getRole())) {
                String token = tokenProvider.generateToken(user.getUsername(), user.getRole());
                return new AuthResponse(token, user.getRole(), user.getId(), user.getUsername());
            }
        }
        
        throw new RuntimeException("Invalid credentials");
    }

    public static class LoginRequest {
        private String username;
        private String password;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class AuthResponse {
        private String token;
        private String role;
        private String userId;
        private String username;

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