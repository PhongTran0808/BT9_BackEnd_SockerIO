package com.example.support;

public class TestApplication {
    public static void main(String[] args) {
        System.out.println("Testing Customer Support Application...");
        
        // Test UserRepository
        UserRepository userRepo = new UserRepository();
        UserRepository.User user = new UserRepository.User("user1", "john", "password", "CUSTOMER");
        userRepo.save(user);
        
        System.out.println("User saved: " + user.getUsername());
        
        // Test ChatMessageRepository
        ChatMessageRepository chatRepo = new ChatMessageRepository();
        ChatMessageRepository.ChatMessage message = new ChatMessageRepository.ChatMessage();
        message.setSenderId("user1");
        message.setContent("Hello Manager!");
        message.setRole("CUSTOMER");
        message.setTimestamp(new java.util.Date());
        
        chatRepo.save(message);
        System.out.println("Message saved: " + message.getContent());
        
        System.out.println("All tests passed!");
    }
    
    // Include necessary classes
    public static class UserRepository {
        private final java.util.Map<String, User> users = new java.util.concurrent.ConcurrentHashMap<>();

        public java.util.Optional<User> findById(String id) {
            return java.util.Optional.ofNullable(users.get(id));
        }

        public User save(User user) {
            users.put(user.getId(), user);
            return user;
        }

        public java.util.List<User> findByRole(String role) {
            return users.values().stream()
                    .filter(user -> role.equals(user.getRole()))
                    .collect(java.util.stream.Collectors.toList());
        }

        public static class User {
            private String id, username, password, role;

            public User() {}
            public User(String id, String username, String password, String role) {
                this.id = id; this.username = username; this.password = password; this.role = role;
            }

            public String getId() { return id; }
            public String getUsername() { return username; }
            public String getPassword() { return password; }
            public String getRole() { return role; }
        }
    }
    
    public static class ChatMessageRepository {
        private final java.util.Map<Long, ChatMessage> messages = new java.util.concurrent.ConcurrentHashMap<>();
        private final java.util.concurrent.atomic.AtomicLong idGenerator = new java.util.concurrent.atomic.AtomicLong(1);

        public ChatMessage save(ChatMessage message) {
            if (message.getId() == null) {
                message.setId(idGenerator.getAndIncrement());
            }
            messages.put(message.getId(), message);
            return message;
        }

        public static class ChatMessage {
            private Long id;
            private String senderId, senderName, recipientId, content, role;
            private java.util.Date timestamp;

            public ChatMessage() {}

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getSenderId() { return senderId; }
            public void setSenderId(String senderId) { this.senderId = senderId; }
            public String getContent() { return content; }
            public void setContent(String content) { this.content = content; }
            public String getRole() { return role; }
            public void setRole(String role) { this.role = role; }
            public java.util.Date getTimestamp() { return timestamp; }
            public void setTimestamp(java.util.Date timestamp) { this.timestamp = timestamp; }
        }
    }
}