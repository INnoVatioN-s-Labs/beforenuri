package com.toyproject.t4lk.room;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRooms() {
        return roomRepository.findAllByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoom(Long roomId) {
        return roomRepository.findByIdAndIsDeletedFalse(roomId)
                .map(this::toResponse)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    public RoomResponse createRoom(RoomUpsertRequest request) {
        validateDuplicateCode(request.code(), null);
        Room room = roomRepository.save(new Room(
                request.code(),
                request.title(),
                request.description(),
                request.category(),
                request.active()
        ));
        return toResponse(room);
    }

    public RoomResponse updateRoom(Long roomId, RoomUpsertRequest request) {
        Room room = getRoomEntity(roomId);
        validateDuplicateCode(request.code(), roomId);
        room.update(
                request.code(),
                request.title(),
                request.description(),
                request.category(),
                request.active()
        );
        return toResponse(room);
    }

    public void deleteRoom(Long roomId) {
        Room room = getRoomEntity(roomId);
        room.delete();
    }

    @Transactional(readOnly = true)
    public Room getRoomEntity(Long roomId) {
        return roomRepository.findByIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    private void validateDuplicateCode(Integer code, Long currentRoomId) {
        roomRepository.findByCodeAndIsDeletedFalse(code)
                .filter(room -> !room.getId().equals(currentRoomId))
                .ifPresent(room -> {
                    throw new RoomCodeAlreadyExistsException(code);
                });
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getCode(),
                room.getTitle(),
                room.getDescription(),
                room.getCategory(),
                room.isActive()
        );
    }
}
