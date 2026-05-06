package com.toyproject.t4lk.chat;

import com.toyproject.t4lk.common.BaseEntity;
import com.toyproject.t4lk.room.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 120)
    private String senderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessageType messageType;

    @Column(nullable = false, length = 500)
    private String content;

    protected ChatMessage() {
    }

    public ChatMessage(Room room, String senderName, ChatMessageType messageType, String content) {
        this.room = room;
        this.senderName = senderName;
        this.messageType = messageType;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Room getRoom() {
        return room;
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
}
