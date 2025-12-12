package com.example.support.service;

import com.example.support.model.ChatMessage;
import com.example.support.repository.ChatMessageRepository;
import java.util.Date;
import java.util.List;

public class ChatService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage saveMessage(String senderId, String senderName, String recipientId, String content, String role) {
        final ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setRole(role);
        message.setTimestamp(new Date());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getAllMessages() {
        return chatMessageRepository.findAll();
    }
}