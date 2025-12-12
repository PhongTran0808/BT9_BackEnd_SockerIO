package com.example.support;

public class JwtTokenProvider {
    
    public String generateToken(String username, String role) {
        // Simple token format: username:role:timestamp
        return username + ":" + role + ":" + System.currentTimeMillis();
    }
    
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = token.split(":");
        return parts.length == 3;
    }
    
    public String getUsernameFromToken(String token) {
        if (token == null) return null;
        String[] parts = token.split(":");
        return parts.length > 0 ? parts[0] : null;
    }
    
    public String getRoleFromToken(String token) {
        if (token == null) return null;
        String[] parts = token.split(":");
        return parts.length > 1 ? parts[1] : null;
    }
}