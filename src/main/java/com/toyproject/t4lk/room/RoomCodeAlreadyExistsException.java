package com.toyproject.t4lk.room;

public class RoomCodeAlreadyExistsException extends RuntimeException {

    public RoomCodeAlreadyExistsException(Integer code) {
        super("이미 사용 중인 채팅방 코드입니다. code=" + code);
    }
}
