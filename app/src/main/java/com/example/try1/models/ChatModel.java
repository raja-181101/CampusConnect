package com.example.try1.models;

public class ChatModel {
    private String messageId;
    private String message;
    private String senderId;
    private String receiverId;
    private long timestamp;
    private boolean seen;
    private String type; // "text", "image"

    public ChatModel() {}

    public ChatModel(String message, String senderId, String receiverId, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.seen = false;
        this.type = "text";
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
