package com.example.support.security;

public class SecurityConfig {
    // Simplified security configuration
    // In a real application, this would configure Spring Security
    
    public boolean isAllowedPath(String path) {
        // Allow all paths for demo purposes
        return true;
    }
    
    public boolean validateRequest(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        final JwtTokenProvider tokenProvider = new JwtTokenProvider();
        return tokenProvider.validateToken(token);
    }
}