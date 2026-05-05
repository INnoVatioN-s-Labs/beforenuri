package com.toyproject.t4lk.room;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private static final List<RoomResponse> ROOMS = List.of(
            new RoomResponse(1L, "자유 대화실", "누구나 편하게 이야기하는 기본 방", true),
            new RoomResponse(2L, "심야 잡담방", "밤 시간대 가볍게 이야기하는 방", true),
            new RoomResponse(3L, "추억의 PC통신방", "레트로 감성으로 대화하는 컨셉 방", false)
    );

    public List<RoomResponse> getRooms() {
        return ROOMS;
    }

    public RoomResponse getRoom(Long roomId) {
        return ROOMS.stream()
                .filter(room -> room.id().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }
}
