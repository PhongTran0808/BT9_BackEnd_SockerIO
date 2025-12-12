package com.example.support;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketIOServer {
    private static UserRepository userRepository = new UserRepository();
    private static ChatMessageRepository chatRepository = new ChatMessageRepository();
    private static JwtTokenProvider tokenProvider = new JwtTokenProvider();
    private static AuthController authController = new AuthController(userRepository, tokenProvider);
    private static ChatController chatController = new ChatController(chatRepository, userRepository);
    private static ManagerController managerController = new ManagerController(userRepository);
    
    // Lưu trữ client connections theo userId
    private static Map<String, SocketIOClient> userClients = new ConcurrentHashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        config.setOrigin("*"); // Allow CORS for Android
        
        final com.corundumstudio.socketio.SocketIOServer server = 
            new com.corundumstudio.socketio.SocketIOServer(config);
        
        // Connection event
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("🔗 Client connected: " + client.getSessionId());
            }
        });
        
        // Disconnect event
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println("❌ Client disconnected: " + client.getSessionId());
                // Remove from userClients map
                userClients.entrySet().removeIf(entry -> entry.getValue().equals(client));
            }
        });
        
        // Login event
        server.addEventListener("login", LoginData.class, new DataListener<LoginData>() {
            @Override
            public void onData(SocketIOClient client, LoginData data, com.corundumstudio.socketio.AckRequest ackSender) {
                try {
                    System.out.println("📥 Login request: " + data.username + " as " + data.role);
                    
                    AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
                    loginRequest.setUsername(data.username);
                    loginRequest.setPassword("default");
                    loginRequest.setRole(data.role);
                    
                    AuthController.AuthResponse response = authController.login(loginRequest);
                    
                    // Lưu client connection
                    userClients.put(response.getUserId(), client);
                    
                    // Gửi response về client
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.success = true;
                    loginResponse.token = response.getToken();
                    loginResponse.role = response.getRole();
                    loginResponse.userId = response.getUserId();
                    loginResponse.username = response.getUsername();
                    
                    client.sendEvent("login_response", loginResponse);
                    System.out.println("📤 Login successful for: " + data.username);
                    
                } catch (Exception e) {
                    System.err.println("❌ Login error: " + e.getMessage());
                    
                    LoginResponse errorResponse = new LoginResponse();
                    errorResponse.success = false;
                    errorResponse.error = e.getMessage();
                    
                    client.sendEvent("login_response", errorResponse);
                }
            }
        });
        
        // Send message event
        server.addEventListener("send_message", MessageData.class, new DataListener<MessageData>() {
            @Override
            public void onData(SocketIOClient client, MessageData data, com.corundumstudio.socketio.AckRequest ackSender) {
                try {
                    System.out.println("📥 Message from " + data.senderName + ": " + data.content);
                    
                    ChatController.MessagePayload payload = new ChatController.MessagePayload();
                    payload.setSenderId(data.senderId);
                    payload.setSenderName(data.senderName);
                    payload.setRecipientId(data.recipientId != null ? data.recipientId : "manager");
                    payload.setContent(data.content);
                    payload.setRole(data.role != null ? data.role : "CUSTOMER");
                    
                    // Lưu tin nhắn vào database
                    if ("CUSTOMER".equals(payload.getRole())) {
                        chatController.sendMessageToManager(payload);
                    } else {
                        chatController.replyToUser(payload);
                    }
                    
                    // Gửi tin nhắn đến recipient
                    String recipientId = payload.getRecipientId();
                    SocketIOClient recipientClient = userClients.get(recipientId);
                    
                    if (recipientClient != null) {
                        MessageNotification notification = new MessageNotification();
                        notification.id = System.currentTimeMillis();
                        notification.senderId = data.senderId;
                        notification.senderName = data.senderName;
                        notification.content = data.content;
                        notification.timestamp = System.currentTimeMillis();
                        notification.isFromCustomer = "CUSTOMER".equals(payload.getRole());
                        
                        recipientClient.sendEvent("new_message", notification);
                        System.out.println("📤 Message delivered to: " + recipientId);
                    } else {
                        System.out.println("⚠️ Recipient " + recipientId + " is not online");
                    }
                    
                    // Confirm message sent
                    MessageResponse response = new MessageResponse();
                    response.success = true;
                    response.message = "Message sent successfully";
                    
                    client.sendEvent("message_response", response);
                    
                } catch (Exception e) {
                    System.err.println("❌ Message error: " + e.getMessage());
                    
                    MessageResponse errorResponse = new MessageResponse();
                    errorResponse.success = false;
                    errorResponse.error = e.getMessage();
                    
                    client.sendEvent("message_response", errorResponse);
                }
            }
        });
        
        // Get messages event
        server.addEventListener("get_messages", GetMessagesData.class, new DataListener<GetMessagesData>() {
            @Override
            public void onData(SocketIOClient client, GetMessagesData data, com.corundumstudio.socketio.AckRequest ackSender) {
                try {
                    System.out.println("📥 Get messages request for: " + data.userId);
                    
                    var messages = chatRepository.getMessagesByUser(data.userId);
                    
                    MessagesResponse response = new MessagesResponse();
                    response.success = true;
                    response.messages = messages.stream()
                        .map(msg -> {
                            MessageNotification notification = new MessageNotification();
                            notification.id = msg.getId();
                            notification.senderId = msg.getSenderId();
                            notification.senderName = msg.getSenderName();
                            notification.content = msg.getContent();
                            notification.timestamp = msg.getTimestampMillis();
                            notification.isFromCustomer = msg.getSenderId().startsWith("customer");
                            return notification;
                        })
                        .toArray(MessageNotification[]::new);
                    
                    client.sendEvent("messages_response", response);
                    System.out.println("📤 Sent " + messages.size() + " messages to: " + data.userId);
                    
                } catch (Exception e) {
                    System.err.println("❌ Get messages error: " + e.getMessage());
                    
                    MessagesResponse errorResponse = new MessagesResponse();
                    errorResponse.success = false;
                    errorResponse.error = e.getMessage();
                    
                    client.sendEvent("messages_response", errorResponse);
                }
            }
        });
        
        // Get customers event (for manager)
        server.addEventListener("get_customers", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient client, Object data, com.corundumstudio.socketio.AckRequest ackSender) {
                try {
                    System.out.println("📥 Get customers request");
                    
                    var customers = managerController.getCustomers();
                    
                    CustomersResponse response = new CustomersResponse();
                    response.success = true;
                    response.customers = customers.stream()
                        .map(customer -> {
                            CustomerInfo info = new CustomerInfo();
                            info.id = customer.getId();
                            info.username = customer.getUsername();
                            info.role = customer.getRole();
                            info.isOnline = userClients.containsKey(customer.getId());
                            return info;
                        })
                        .toArray(CustomerInfo[]::new);
                    
                    client.sendEvent("customers_response", response);
                    System.out.println("📤 Sent " + customers.size() + " customers");
                    
                } catch (Exception e) {
                    System.err.println("❌ Get customers error: " + e.getMessage());
                    
                    CustomersResponse errorResponse = new CustomersResponse();
                    errorResponse.success = false;
                    errorResponse.error = e.getMessage();
                    
                    client.sendEvent("customers_response", errorResponse);
                }
            }
        });
        
        server.start();
        
        System.out.println("🚀 Socket.IO Customer Support Server started!");
        System.out.println("📱 Server running on: ws://localhost:9092");
        System.out.println("📡 Android app should connect to: ws://10.0.2.2:9092");
        System.out.println("⚡ Real-time events:");
        System.out.println("   - login: Authenticate user");
        System.out.println("   - send_message: Send message to recipient");
        System.out.println("   - get_messages: Get message history");
        System.out.println("   - get_customers: Get customer list (manager only)");
        System.out.println("   - new_message: Receive real-time messages");
        System.out.println("⏹️  Press Ctrl+C to stop server");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            System.out.println("🛑 Socket.IO Server stopped");
        }));
    }
    
    // Data classes for Socket.IO events
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
    
    public static class MessageData {
        public String senderId;
        public String senderName;
        public String recipientId;
        public String content;
        public String role;
    }
    
    public static class MessageResponse {
        public boolean success;
        public String message;
        public String error;
    }
    
    public static class MessageNotification {
        public Long id;
        public String senderId;
        public String senderName;
        public String content;
        public long timestamp;
        public boolean isFromCustomer;
    }
    
    public static class GetMessagesData {
        public String userId;
    }
    
    public static class MessagesResponse {
        public boolean success;
        public MessageNotification[] messages;
        public String error;
    }
    
    public static class CustomersResponse {
        public boolean success;
        public CustomerInfo[] customers;
        public String error;
    }
    
    public static class CustomerInfo {
        public String id;
        public String username;
        public String role;
        public boolean isOnline;
    }
}