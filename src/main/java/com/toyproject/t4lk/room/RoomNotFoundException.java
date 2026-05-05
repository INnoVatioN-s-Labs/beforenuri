package com.toyproject.t4lk.room;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long roomId) {
        super("존재하지 않는 채팅방입니다. roomId=" + roomId);
    }
}
