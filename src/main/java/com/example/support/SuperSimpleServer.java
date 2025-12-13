package com.example.support;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;

public class SuperSimpleServer {
    
    public static void main(String[] args) {
        // SUPER SIMPLE CONFIG - WEBSOCKET ONLY
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9092);
        config.setOrigin("*");
        
        // SUPPORT BOTH TRANSPORTS FOR COMPATIBILITY
        config.setTransports(Transport.POLLING, Transport.WEBSOCKET);
        
        // Very generous timeouts
        config.setPingTimeout(120000);   // 2 minutes
        config.setPingInterval(60000);   // 1 minute
        
        SocketIOServer server = new SocketIOServer(config);
        
        // Simple connect
        server.addConnectListener(client -> {
            System.out.println("✅ CONNECTED: " + client.getSessionId());
            client.sendEvent("connected", "OK");
        });
        
        // Simple disconnect
        server.addDisconnectListener(client -> {
            System.out.println("❌ DISCONNECTED: " + client.getSessionId());
        });
        
        // Simple test
        server.addEventListener("test", String.class, (client, data, ack) -> {
            System.out.println("📥 TEST: " + data);
            client.sendEvent("test_response", "Got: " + data);
        });
        
        // Simple login with detailed logging
        server.addEventListener("login", Object.class, (client, data, ack) -> {
            System.out.println("📥 LOGIN REQUEST RECEIVED!");
            System.out.println("   Client: " + client.getSessionId());
            System.out.println("   Data: " + data);
            System.out.println("   Data type: " + (data != null ? data.getClass().getSimpleName() : "null"));
            
            String response = "{\"success\":true,\"token\":\"test123\",\"username\":\"testuser\"}";
            client.sendEvent("login_response", response);
            System.out.println("✅ LOGIN RESPONSE SENT: " + response);
        });
        
        try {
            server.start();
            System.out.println("🚀 SUPER SIMPLE SERVER STARTED!");
            System.out.println("📍 WebSocket: ws://localhost:9092");
            System.out.println("📱 Android: ws://10.0.2.2:9092");
            System.out.println("🔥 SUPPORTS BOTH POLLING & WEBSOCKET!");
            
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("💥 ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}