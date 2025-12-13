package com.example.support;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSocketIOServer {
    private static Map<String, SocketIOClient> userClients = new ConcurrentHashMap<>();
    private static UserRepository userRepository = new UserRepository();
    private static JwtTokenProvider tokenProvider = new JwtTokenProvider();
    private static AuthController authController = new AuthController(userRepository, tokenProvider);

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9092);
        
        // CORS and connection settings
        config.setOrigin("*");
        config.setAllowCustomRequests(true);
        
        // Increase timeouts to prevent disconnections
        config.setPingTimeout(120000); // 2 minutes
        config.setPingInterval(60000);  // 1 minute
        config.setUpgradeTimeout(30000); // 30 seconds
        
        // Allow larger payloads
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        
        // Enable both transports
        config.setTransports(
            com.corundumstudio.socketio.Transport.POLLING,
            com.corundumstudio.socketio.Transport.WEBSOCKET
        );
        
        final com.corundumstudio.socketio.SocketIOServer server = 
            new com.corundumstudio.socketio.SocketIOServer(config);
        
        // Connection events
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("✅ Client connected: " + client.getSessionId());
                System.out.println("   Remote Address: " + client.getRemoteAddress());
                System.out.println("   Transport: " + client.getTransport());
                
                // Send welcome message
                client.sendEvent("connection_confirmed", "Welcome to Socket.IO server!");
                
                // Send ping to keep connection alive
                client.sendEvent("server_ping", System.currentTimeMillis());
            }
        });
        
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println("❌ Client disconnected: " + client.getSessionId());
                System.out.println("   Reason: Connection lost or client closed");
                
                // Remove from active users
                String disconnectedUser = null;
                for (Map.Entry<String, SocketIOClient> entry : userClients.entrySet()) {
                    if (entry.getValue().equals(client)) {
                        disconnectedUser = entry.getKey();
                        break;
                    }
                }
                
                if (disconnectedUser != null) {
                    userClients.remove(disconnectedUser);
                    System.out.println("   User removed: " + disconnectedUser);
                }
                
                System.out.println("   Active users: " + userClients.size());
            }
        });
        
        // Ping/Pong to keep connection alive
        server.addEventListener("client_ping", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient client, Object data, com.corundumstudio.socketio.AckRequest ackSender) {
                // Respond to client ping
                client.sendEvent("server_pong", System.currentTimeMillis());
            }
        });
        
        // Test event
        server.addEventListener("test_connection", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, com.corundumstudio.socketio.AckRequest ackSender) {
                System.out.println("🧪 Test received: " + data);
                client.sendEvent("test_response", "Server received: " + data);
            }
        });
        
        // Login event
        server.addEventListener("login", LoginData.class, new DataListener<LoginData>() {
            @Override
            public void onData(SocketIOClient client, LoginData data, com.corundumstudio.socketio.AckRequest ackSender) {
                try {
                    System.out.println("📥 Login: " + data.username + " as " + data.role);
                    
                    AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
                    loginRequest.setUsername(data.username);
                    loginRequest.setPassword("default");
                    loginRequest.setRole(data.role);
                    
                    AuthController.AuthResponse response = authController.login(loginRequest);
                    userClients.put(response.getUserId(), client);
                    
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.success = true;
                    loginResponse.token = response.getToken();
                    loginResponse.role = response.getRole();
                    loginResponse.userId = response.getUserId();
                    loginResponse.username = response.getUsername();
                    
                    client.sendEvent("login_response", loginResponse);
                    System.out.println("✅ Login successful: " + data.username);
                    
                } catch (Exception e) {
                    System.err.println("❌ Login error: " + e.getMessage());
                    
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.success = false;
                    errorResponse.error = e.getMessage();
                    
                    client.sendEvent("login_response", errorResponse);
                }
            }
        });
        
        try {
            server.start();
            
            System.out.println("🚀 Simple Socket.IO Server started!");
            System.out.println("📱 Server: ws://localhost:9092");
            System.out.println("📱 Android: ws://10.0.2.2:9092");
            System.out.println("⏹️  Press Ctrl+C to stop");
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
    
    // Data classes
    public static class LoginData {
        public String username;
        public String role;
    }
    
    public static class LoginResponse {
        public boolean success;
        public String token;
        public String role;
        public String userId;
        public String username;
        public String error;
    }
}