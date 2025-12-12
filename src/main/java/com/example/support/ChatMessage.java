package com.example.support;

import java.util.Date;

public class ChatMessage {
    private Long id;
    private String senderId;
    private String senderName;
    private String recipientId;
    private String content;
    private String role;
    private Date timestamp;

    public ChatMessage() {}

    public ChatMessage(String senderId, String senderName, String recipientId, String content, String role, Date timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.content = content;
        this.role = role;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public long getTimestampMillis() { 
        return timestamp != null ? timestamp.getTime() : System.currentTimeMillis(); 
    }
}