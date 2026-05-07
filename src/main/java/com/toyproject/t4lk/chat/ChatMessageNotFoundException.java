package com.toyproject.t4lk.chat;

public class ChatMessageNotFoundException extends RuntimeException {

    public ChatMessageNotFoundException(Long roomId, Long messageId) {
        super("존재하지 않는 메시지입니다. roomId=" + roomId + ", messageId=" + messageId);
    }
}
