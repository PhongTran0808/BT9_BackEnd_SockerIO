package com.example.support.config;

public class AppConfig {
    // Simplified configuration
    private final boolean corsEnabled = true;
    private final String[] allowedOrigins = {"*"};
    private final String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};

    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    public String[] getAllowedOrigins() {
        return allowedOrigins;
    }

    public String[] getAllowedMethods() {
        return allowedMethods;
    }
}