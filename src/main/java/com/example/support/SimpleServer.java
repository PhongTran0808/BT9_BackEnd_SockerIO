package com.example.support;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SimpleServer {
    private static UserRepository userRepository = new UserRepository();
    private static ChatMessageRepository chatRepository = new ChatMessageRepository();
    private static JwtTokenProvider tokenProvider = new JwtTokenProvider();
    private static AuthController authController = new AuthController(userRepository, tokenProvider);
    private static ChatController chatController = new ChatController(chatRepository, userRepository);
    private static ManagerController managerController = new ManagerController(userRepository);

    public static void main(String[] args) throws IOException {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Root handler
        server.createContext("/", new RootHandler());
        
        // API endpoints
        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/chat/send", new SendMessageHandler());
        server.createContext("/api/chat/messages", new GetMessagesHandler());
        server.createContext("/api/manager/customers", new GetCustomersHandler());
        server.createContext("/api/health", new HealthHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("🚀 Customer Support Server started on http://localhost:8080");
        System.out.println("📱 Android app can now connect to:");
        System.out.println("   - Login: POST http://localhost:8080/api/auth/login");
        System.out.println("   - Send Message: POST http://localhost:8080/api/chat/send");
        System.out.println("   - Get Messages: GET http://localhost:8080/api/chat/messages?userId=customer1");
        System.out.println("   - Get Customers: GET http://localhost:8080/api/manager/customers");
        System.out.println("   - Health Check: GET http://localhost:8080/api/health");
        System.out.println("⏹️  Press Ctrl+C to stop server");
    }

    // Login Handler
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                System.out.println("📥 Login request: " + requestBody);
                
                Map<String, String> loginData = parseJson(requestBody);
                
                AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
                loginRequest.setUsername(loginData.get("username"));
                loginRequest.setPassword("default"); // Không cần password thật
                loginRequest.setRole(loginData.get("role"));
                
                AuthController.AuthResponse response = authController.login(loginRequest);
                
                String jsonResponse = String.format(
                    "{\"token\":\"%s\",\"role\":\"%s\",\"userId\":\"%s\",\"username\":\"%s\"}",
                    response.getToken(), response.getRole(), response.getUserId(), response.getUsername()
                );
                
                System.out.println("📤 Login response: " + jsonResponse);
                sendJsonResponse(exchange, 200, jsonResponse);
                
            } catch (Exception e) {
                System.err.println("❌ Login error: " + e.getMessage());
                sendError(exchange, 400, "Invalid request: " + e.getMessage());
            }
        }
    }

    // Send Message Handler
    static class SendMessageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                String requestBody = readRequestBody(exchange);
                System.out.println("📥 Message request: " + requestBody);
                
                Map<String, String> messageData = parseJson(requestBody);
                
                ChatController.MessagePayload payload = new ChatController.MessagePayload();
                payload.setSenderId(messageData.get("senderId"));
                payload.setSenderName(messageData.get("senderName"));
                payload.setRecipientId(messageData.getOrDefault("recipientId", "manager"));
                payload.setContent(messageData.get("content"));
                payload.setRole(messageData.getOrDefault("role", "CUSTOMER"));
                
                if ("CUSTOMER".equals(payload.getRole())) {
                    chatController.sendMessageToManager(payload);
                } else {
                    chatController.replyToUser(payload);
                }
                
                String jsonResponse = "{\"status\":\"success\",\"message\":\"Message sent successfully\"}";
                System.out.println("📤 Message response: " + jsonResponse);
                sendJsonResponse(exchange, 200, jsonResponse);
                
            } catch (Exception e) {
                System.err.println("❌ Message error: " + e.getMessage());
                sendError(exchange, 400, "Invalid request: " + e.getMessage());
            }
        }
    }

    // Get Messages Handler
    static class GetMessagesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                // Lấy userId từ query parameter
                String query = exchange.getRequestURI().getQuery();
                String userId = extractUserIdFromQuery(query);
                
                if (userId == null || userId.isEmpty()) {
                    sendError(exchange, 400, "Missing userId parameter");
                    return;
                }
                
                var messages = chatRepository.getMessagesByUser(userId);
                
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < messages.size(); i++) {
                    ChatMessage msg = messages.get(i);
                    json.append(String.format(
                        "{\"id\":\"%s\",\"senderId\":\"%s\",\"senderName\":\"%s\",\"content\":\"%s\",\"timestamp\":%d,\"isFromCustomer\":%s}",
                        msg.getId(), msg.getSenderId(), msg.getSenderName(), 
                        msg.getContent().replace("\"", "\\\""), msg.getTimestampMillis(), 
                        msg.getSenderId().startsWith("customer")
                    ));
                    if (i < messages.size() - 1) json.append(",");
                }
                json.append("]");
                
                System.out.println("📤 Messages response for " + userId + ": " + json.toString());
                sendJsonResponse(exchange, 200, json.toString());
                
            } catch (Exception e) {
                System.err.println("❌ Get messages error: " + e.getMessage());
                sendError(exchange, 500, "Server error: " + e.getMessage());
            }
        }
    }

    // Get Customers Handler
    static class GetCustomersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                var customers = managerController.getCustomers();
                StringBuilder json = new StringBuilder("[");
                
                for (int i = 0; i < customers.size(); i++) {
                    User customer = customers.get(i);
                    json.append(String.format(
                        "{\"id\":\"%s\",\"username\":\"%s\",\"role\":\"%s\"}",
                        customer.getId(), customer.getUsername(), customer.getRole()
                    ));
                    if (i < customers.size() - 1) json.append(",");
                }
                json.append("]");
                
                System.out.println("📤 Customers response: " + json.toString());
                sendJsonResponse(exchange, 200, json.toString());
                
            } catch (Exception e) {
                System.err.println("❌ Get customers error: " + e.getMessage());
                sendError(exchange, 500, "Server error: " + e.getMessage());
            }
        }
    }

    // Root Handler
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            String path = exchange.getRequestURI().getPath();
            
            // If accessing root, show welcome message
            if ("/".equals(path)) {
                String response = "{\n" +
                    "  \"message\": \"🚀 Customer Support API Server\",\n" +
                    "  \"status\": \"running\",\n" +
                    "  \"endpoints\": {\n" +
                    "    \"login\": \"POST /api/auth/login\",\n" +
                    "    \"send_message\": \"POST /api/chat/send\",\n" +
                    "    \"get_messages\": \"GET /api/chat/messages?userId=customer1\",\n" +
                    "    \"get_customers\": \"GET /api/manager/customers\",\n" +
                    "    \"health_check\": \"GET /api/health\"\n" +
                    "  },\n" +
                    "  \"example_usage\": {\n" +
                    "    \"login\": \"curl -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{\\\"username\\\":\\\"customer1\\\",\\\"role\\\":\\\"CUSTOMER\\\"}'\",\n" +
                    "    \"health\": \"curl http://localhost:8080/api/health\"\n" +
                    "  }\n" +
                    "}";
                sendJsonResponse(exchange, 200, response);
                return;
            }
            
            // For other paths, show 404 with available endpoints
            String response = "{\n" +
                "  \"error\": \"Endpoint not found\",\n" +
                "  \"requested_path\": \"" + path + "\",\n" +
                "  \"available_endpoints\": [\n" +
                "    \"GET /\",\n" +
                "    \"POST /api/auth/login\",\n" +
                "    \"POST /api/chat/send\",\n" +
                "    \"GET /api/chat/messages?userId=customer1\",\n" +
                "    \"GET /api/manager/customers\",\n" +
                "    \"GET /api/health\"\n" +
                "  ]\n" +
                "}";
            sendJsonResponse(exchange, 404, response);
        }
    }

    // Health Check Handler
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            String response = "{\"status\":\"OK\",\"message\":\"Customer Support Server is running\",\"timestamp\":" + System.currentTimeMillis() + "}";
            sendJsonResponse(exchange, 200, response);
        }
    }

    // Helper methods
    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        
        if (json == null || json.trim().isEmpty()) {
            return result;
        }
        
        json = json.trim().replaceAll("[{}]", "");
        
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            if (pair != null && pair.contains(":")) {
                String[] keyValue = pair.split(":", 2); // Limit to 2 parts
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    
                    if (key != null && !key.isEmpty()) {
                        result.put(key, value != null ? value : "");
                    }
                }
            }
        }
        return result;
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        sendJsonResponse(exchange, statusCode, response);
    }
    
    private static String extractUserIdFromQuery(String query) {
        if (query == null) return null;
        
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }
}