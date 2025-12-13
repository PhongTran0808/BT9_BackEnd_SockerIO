package com.example.support;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UltimateSocketIOServer {
    private static Map<String, SocketIOClient> clients = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        // IMPROVED CONFIGURATION FOR ANDROID
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0"); // Listen on all interfaces
        config.setPort(9092);
        
        // Android-friendly settings
        config.setOrigin("*");
        config.setPingTimeout(60000);    // 1 minute
        config.setPingInterval(25000);   // 25 seconds
        config.setUpgradeTimeout(10000); // 10 seconds
        config.setMaxFramePayloadLength(1048576); // 1MB
        config.setMaxHttpContentLength(1048576);  // 1MB
        
        // Enable all transports
        config.setTransports(com.corundumstudio.socketio.Transport.POLLING, 
                           com.corundumstudio.socketio.Transport.WEBSOCKET);
        
        SocketIOServer server = new SocketIOServer(config);
        
        // BASIC EVENTS ONLY
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                String sessionId = client.getSessionId().toString();
                String remoteAddress = client.getRemoteAddress().toString();
                clients.put(sessionId, client);
                System.out.println("✅ CONNECTED: " + sessionId + " from " + remoteAddress);
                System.out.println("📊 Total clients: " + clients.size());
                
                // Immediate response to confirm connection
                client.sendEvent("connected", "Welcome! Server time: " + System.currentTimeMillis());
            }
        });
        
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                String sessionId = client.getSessionId().toString();
                clients.remove(sessionId);
                System.out.println("❌ DISCONNECTED: " + sessionId);
                System.out.println("📊 Remaining clients: " + clients.size());
            }
        });
        
        // SIMPLE TEST EVENT
        server.addEventListener("test", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, com.corundumstudio.socketio.AckRequest ackSender) {
                System.out.println("📥 TEST: " + data);
                client.sendEvent("test_response", "Server got: " + data);
            }
        });
        
        // SIMPLE LOGIN EVENT
        server.addEventListener("login", LoginData.class, new DataListener<LoginData>() {
            @Override
            public void onData(SocketIOClient client, LoginData data, com.corundumstudio.socketio.AckRequest ackSender) {
                System.out.println("📥 LOGIN: " + data.username);
                
                LoginResponse response = new LoginResponse();
                response.success = true;
                response.username = data.username;
                response.token = "token_" + data.username + "_" + System.currentTimeMillis();
                
                client.sendEvent("login_response", response);
                System.out.println("✅ LOGIN OK: " + data.username);
            }
        });
        
        // HEARTBEAT EVENT
        server.addEventListener("ping", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, com.corundumstudio.socketio.AckRequest ackSender) {
                System.out.println("💓 PING from: " + client.getSessionId());
                client.sendEvent("pong", "Server alive: " + System.currentTimeMillis());
            }
        });
        
        try {
            server.start();
            System.out.println("🚀 ULTIMATE SOCKET.IO SERVER STARTED!");
            System.out.println("📍 URL: http://localhost:9092");
            System.out.println("📱 Android: http://10.0.2.2:9092");
            System.out.println("🔥 ULTRA SIMPLE - NO FANCY CONFIG!");
            System.out.println("⏹️  Press Ctrl+C to stop");
            
            // Keep alive
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("💥 SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static class LoginData {
        public String username;
        public String role;
    }
    
    public static class LoginResponse {
        public boolean success;
        public String username;
        public String token;
        public String error;
    }
}