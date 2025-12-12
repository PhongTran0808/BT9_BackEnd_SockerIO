package com.example.support.config;

public class WebSocketConfig {
    // Simplified WebSocket configuration
    private final String endpoint = "/ws";
    private final String[] allowedOrigins = {"*"};
    private final String[] topicPrefixes = {"/topic", "/queue"};
    private final String applicationDestinationPrefix = "/app";
    private final String userDestinationPrefix = "/user";

    public String getEndpoint() {
        return endpoint;
    }

    public String[] getAllowedOrigins() {
        return allowedOrigins;
    }

    public String[] getTopicPrefixes() {
        return topicPrefixes;
    }

    public String getApplicationDestinationPrefix() {
        return applicationDestinationPrefix;
    }

    public String getUserDestinationPrefix() {
        return userDestinationPrefix;
    }
}