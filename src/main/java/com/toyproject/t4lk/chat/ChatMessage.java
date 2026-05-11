package com.toyproject.t4lk.chat;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private Long roomId;
    private String senderName;
    private ChatMessageType messageType;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    protected ChatMessage() {
    }

    public ChatMessage(Long roomId, String senderName, ChatMessageType messageType, String content) {
        LocalDateTime now = LocalDateTime.now();
        this.roomId = roomId;
        this.senderName = senderName;
        this.messageType = messageType;
        this.content = content;
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false;
    }

    public String getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getSenderName() {
        return senderName;
    }

    public ChatMessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(String senderName, ChatMessageType messageType, String content) {
        this.senderName = senderName;
        this.messageType = messageType;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
