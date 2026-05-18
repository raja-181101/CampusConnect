package com.example.try1.models;

public class ChatListModel {
    private String chatUserId;
    private String chatUserName;
    private String chatUserPhoto;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;
    private boolean isOnline;

    public ChatListModel() {}

    public String getChatUserId() { return chatUserId; }
    public void setChatUserId(String chatUserId) { this.chatUserId = chatUserId; }
    public String getChatUserName() { return chatUserName; }
    public void setChatUserName(String chatUserName) { this.chatUserName = chatUserName; }
    public String getChatUserPhoto() { return chatUserPhoto; }
    public void setChatUserPhoto(String chatUserPhoto) { this.chatUserPhoto = chatUserPhoto; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public long getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(long lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }
}
