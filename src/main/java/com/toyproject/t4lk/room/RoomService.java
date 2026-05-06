package com.toyproject.t4lk.room;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomResponse> getRooms() {
        return roomRepository.findAllByIsDeletedFalseOrderByIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoomResponse getRoom(Long roomId) {
        return roomRepository.findByIdAndIsDeletedFalse(roomId)
                .map(this::toResponse)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    public Room getRoomEntity(Long roomId) {
        return roomRepository.findByIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getTitle(),
                room.getDescription(),
                room.isActive()
        );
    }
}
