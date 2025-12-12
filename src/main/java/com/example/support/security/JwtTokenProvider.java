package com.example.support.security;

public class JwtTokenProvider {
    private final String jwtSecret = "JWTSuperSecretKeyForCustomerSupportApp1234567890";
    private final int jwtExpirationInMs = 604800000;

    // Simplified token generation without JWT library
    public String generateToken(String username, String role) {
        // Simple token format: username:role:timestamp
        long timestamp = System.currentTimeMillis();
        return username + ":" + role + ":" + timestamp;
    }

    public String getUsernameFromJWT(String token) {
        try {
            String[] parts = token.split(":");
            return parts.length > 0 ? parts[0] : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getRoleFromJWT(String token) {
        try {
            String[] parts = token.split(":");
            return parts.length > 1 ? parts[1] : null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String authToken) {
        try {
            String[] parts = authToken.split(":");
            if (parts.length != 3) return false;
            
            long timestamp = Long.parseLong(parts[2]);
            long now = System.currentTimeMillis();
            
            // Check if token is not expired
            return (now - timestamp) < jwtExpirationInMs;
        } catch (Exception ex) {
            return false;
        }
    }
}