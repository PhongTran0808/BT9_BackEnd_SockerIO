package com.example.support;

import java.util.Date;

public class ChatController {
    private final ChatMessageRepository chatRepository;
    private final UserRepository userRepository;

    public ChatController(ChatMessageRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void sendMessageToManager(MessagePayload payload) {
        ChatMessage message = new ChatMessage(
            payload.getSenderId(),
            payload.getSenderName(),
            "manager",
            payload.getContent(),
            payload.getRole(),
            new Date()
        );
        chatRepository.save(message);
        System.out.println("💬 Message from " + payload.getSenderName() + " to manager: " + payload.getContent());
    }

    public void replyToUser(MessagePayload payload) {
        ChatMessage message = new ChatMessage(
            payload.getSenderId(),
            payload.getSenderName(),
            payload.getRecipientId(),
            payload.getContent(),
            payload.getRole(),
            new Date()
        );
        chatRepository.save(message);
        System.out.println("💬 Reply from " + payload.getSenderName() + " to " + payload.getRecipientId() + ": " + payload.getContent());
    }

    public static class MessagePayload {
        private String senderId;
        private String senderName;
        private String recipientId;
        private String content;
        private String role;

        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        
        public String getRecipientId() { return recipientId; }
        public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}