package com.example.support.repository;

import com.example.support.model.ChatMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ChatMessageRepository {
    private final Map<Long, ChatMessage> messages = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ChatMessage save(ChatMessage message) {
        if (message.getId() == null) {
            message.setId(idGenerator.getAndIncrement());
        }
        messages.put(message.getId(), message);
        return message;
    }

    public Optional<ChatMessage> findById(Long id) {
        return Optional.ofNullable(messages.get(id));
    }

    public List<ChatMessage> findAll() {
        return new ArrayList<>(messages.values());
    }

    public void deleteById(Long id) {
        messages.remove(id);
    }
    
    public List<ChatMessage> getMessagesByUser(String userId) {
        List<ChatMessage> userMessages = new ArrayList<>();
        for (ChatMessage message : messages.values()) {
            if (userId.equals(message.getSenderId()) || 
                (message.getRecipientId() != null && userId.equals(message.getRecipientId()))) {
                userMessages.add(message);
            }
        }
        // Sắp xếp theo timestamp
        userMessages.sort((m1, m2) -> Long.compare(m1.getTimestampMillis(), m2.getTimestampMillis()));
        return userMessages;
    }
}