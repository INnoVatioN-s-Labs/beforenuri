package com.toyproject.t4lk.room;

import com.toyproject.t4lk.chat.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        chatMessageRepository.deleteAll();
        roomRepository.deleteAll();

        roomRepository.save(new Room("자유 대화실", "누구나 편하게 이야기하는 기본 방", true));
        roomRepository.save(new Room("심야 잡담방", "밤 시간대 가볍게 이야기하는 방", true));
    }

    @Test
    void getRoomsReturnsPersistedRooms() {
        var rooms = roomService.getRooms();

        assertEquals(2, rooms.size());
        assertEquals("자유 대화실", rooms.get(0).title());
    }

    @Test
    void getRoomReturnsRoomDetail() {
        Long roomId = roomRepository.findAll().get(0).getId();

        var room = roomService.getRoom(roomId);

        assertEquals(roomId, room.id());
        assertEquals("자유 대화실", room.title());
    }

    @Test
    void getRoomThrowsWhenMissing() {
        assertThrows(RoomNotFoundException.class, () -> roomService.getRoom(999L));
    }

    @Test
    void roomEntityInheritsBaseEntityFields() {
        Room savedRoom = roomRepository.findAll().get(0);

        assertNotNull(savedRoom.getCreatedAt());
        assertNotNull(savedRoom.getUpdatedAt());
        assertFalse(savedRoom.isDeleted());
    }
}
