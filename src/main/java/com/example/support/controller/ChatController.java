package com.example.support.controller;

import com.example.support.model.ChatMessage;
import com.example.support.model.User;
import com.example.support.repository.ChatMessageRepository;
import com.example.support.repository.UserRepository;
import java.util.Date;

public class ChatController {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatController(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    // 1. Khách hàng gửi tin nhắn cho Manager
    public void sendMessageToManager(MessagePayload payload) {
        // Lưu tin nhắn
        final ChatMessage message = new ChatMessage();
        message.setSenderId(payload.getSenderId());
        message.setSenderName(payload.getSenderName());
        message.setContent(payload.getContent());
        message.setRole("CUSTOMER");
        message.setTimestamp(new Date());
        chatMessageRepository.save(message);

        // A. Bắn thông báo cho Dashboard của Manager (có khách mới nhắn)
        final User user = userRepository.findById(payload.getSenderId()).orElse(new User(payload.getSenderId(), payload.getSenderName(), "", "CUSTOMER"));
        
        // B. Bắn tin nhắn vào phòng chat riêng của khách này (Để Manager đang chat với khách này sẽ thấy)
        System.out.println("Message sent to manager from: " + payload.getSenderId());
    }

    // 2. Manager trả lời khách hàng
    public void replyToUser(MessagePayload payload) {
        // Lưu tin nhắn
        final ChatMessage message = new ChatMessage();
        message.setSenderId(payload.getSenderId());
        message.setSenderName(payload.getSenderName());
        message.setRecipientId(payload.getRecipientId());
        message.setContent(payload.getContent());
        message.setRole("MANAGER");
        message.setTimestamp(new Date());
        chatMessageRepository.save(message);

        // Gửi tới khách hàng cụ thể
        System.out.println("Reply sent to user: " + payload.getRecipientId());
    }

    public static class MessagePayload {
        private String senderId, senderName, recipientId, content, role;

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