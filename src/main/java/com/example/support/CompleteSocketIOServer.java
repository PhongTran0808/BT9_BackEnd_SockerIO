package com.example.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;

public class CompleteSocketIOServer {
    
    // In-memory user database
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static Map<String, SocketIOClient> connectedClients = new ConcurrentHashMap<>();
    private static List<ChatMessage> messages = new ArrayList<>();
    
    // Initialize some test users
    static {
        users.put("customer1", new User("customer1", "123456", "CUSTOMER"));
        users.put("customer2", new User("customer2", "123456", "CUSTOMER"));
        users.put("customer3", new User("customer3", "123456", "CUSTOMER"));
        users.put("manager", new User("manager", "123456", "MANAGER"));
        users.put("admin", new User("admin", "123456", "MANAGER"));
    }
    
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9092);
        config.setOrigin("*");
        config.setTransports(Transport.POLLING, Transport.WEBSOCKET);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);
        
        SocketIOServer server = new SocketIOServer(config);
        
        // Connection event
        server.addConnectListener(client -> {
            String sessionId = client.getSessionId().toString();
            System.out.println("✅ CLIENT CONNECTED: " + sessionId);
            client.sendEvent("connected", "Server ready for login");
        });
        
        // Disconnect event
        server.addDisconnectListener(client -> {
            String sessionId = client.getSessionId().toString();
            connectedClients.remove(sessionId);
            System.out.println("❌ CLIENT DISCONNECTED: " + sessionId);
        });
        
        // LOGIN EVENT - COMPLETE AUTHENTICATION
        server.addEventListener("login", LoginRequest.class, (client, loginData, ack) -> {
            System.out.println("🔑 LOGIN ATTEMPT:");
            System.out.println("   Username: " + loginData.username);
            System.out.println("   Password: " + loginData.password);
            System.out.println("   Role: " + loginData.role);
            
            // Authenticate user
            User user = users.get(loginData.username);
            System.out.println("   Found user: " + (user != null ? user.username + "/" + user.password + "/" + user.role : "null"));
            
            if (user != null && user.password.equals(loginData.password)) {
                // Login successful
                String token = "token_" + loginData.username + "_" + System.currentTimeMillis();
                connectedClients.put(client.getSessionId().toString(), client);
                client.set("username", loginData.username);
                client.set("role", user.role);
                
                LoginResponse response = new LoginResponse();
                response.success = true;
                response.username = loginData.username;
                response.role = user.role;
                response.token = token;
                response.message = "Login successful";
                
                client.sendEvent("login_response", response);
                System.out.println("✅ LOGIN SUCCESS: " + loginData.username + " (" + user.role + ")");
                
            } else {
                // Login failed
                LoginResponse response = new LoginResponse();
                response.success = false;
                response.error = "Invalid username or password";
                response.message = "Authentication failed";
                
                client.sendEvent("login_response", response);
                System.out.println("❌ LOGIN FAILED: " + loginData.username);
            }
        });
        
        // SEND MESSAGE EVENT
        server.addEventListener("send_message", MessageRequest.class, (client, messageData, ack) -> {
            String username = client.get("username");
            String role = client.get("role");
            
            if (username == null) {
                client.sendEvent("error", "Not authenticated");
                return;
            }
            
            // Create message
            ChatMessage message = new ChatMessage();
            message.id = System.currentTimeMillis();
            message.content = messageData.content;
            message.senderUsername = username;
            message.senderRole = role;
            message.timestamp = System.currentTimeMillis();
            message.roomId = messageData.roomId != null ? messageData.roomId : "general";
            
            messages.add(message);
            
            System.out.println("📨 MESSAGE: " + username + " -> " + message.content);
            
            // Broadcast to all connected clients
            for (SocketIOClient connectedClient : connectedClients.values()) {
                connectedClient.sendEvent("new_message", message);
            }
            
            // Send confirmation to sender
            client.sendEvent("message_sent", "Message delivered");
        });
        
        // GET MESSAGES EVENT
        server.addEventListener("get_messages", Object.class, (client, data, ack) -> {
            String username = client.get("username");
            if (username == null) {
                client.sendEvent("error", "Not authenticated");
                return;
            }
            
            System.out.println("📋 GET MESSAGES: " + username);
            client.sendEvent("messages_list", messages);
        });
        
        // PING EVENT
        server.addEventListener("ping", Object.class, (client, data, ack) -> {
            client.sendEvent("pong", "Server alive: " + System.currentTimeMillis());
        });
        
        try {
            server.start();
            System.out.println("🚀 COMPLETE SOCKET.IO SERVER STARTED!");
            System.out.println("📍 URL: http://localhost:9092");
            System.out.println("📱 Android: http://10.0.2.2:9092");
            System.out.println("👥 Test Users:");
            System.out.println("   customer1/123456 (CUSTOMER)");
            System.out.println("   customer2/123456 (CUSTOMER)");
            System.out.println("   customer3/123456 (CUSTOMER)");
            System.out.println("   manager/123456 (MANAGER)");
            System.out.println("   admin/123456 (MANAGER)");
            System.out.println("⏹️  Press Ctrl+C to stop");
            
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("💥 SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Data classes
    public static class User {
        public String username;
        public String password;
        public String role;
        
        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }
    
    public static class LoginRequest {
        public String username;
        public String password;
        public String role;
    }
    
    public static class LoginResponse {
        public boolean success;
        public String username;
        public String role;
        public String token;
        public String message;
        public String error;
    }
    
    public static class MessageRequest {
        public String content;
        public String roomId;
    }
    
    public static class ChatMessage {
        public long id;
        public String content;
        public String senderUsername;
        public String senderRole;
        public long timestamp;
        public String roomId;
    }
}