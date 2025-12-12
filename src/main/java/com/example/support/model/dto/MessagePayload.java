package com.example.support.model.dto;

public class MessagePayload {
    private String senderId;
    private String senderName;
    private String recipientId;
    private String content;
    private String role;

    public MessagePayload() {}

    public MessagePayload(String senderId, String senderName, String recipientId, String content, String role) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.content = content;
        this.role = role;
    }

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