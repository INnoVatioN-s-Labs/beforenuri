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
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        roomRepository.save(new Room(1, "자유 대화실", "누구나 편하게 이야기하는 기본 방", "평범함이 좋아", true));
        roomRepository.save(new Room(21, "서울특별시", "서울 지역 이용자들이 모이는 방", "지역별 대화실", true));
    }

    @Test
    void getRoomsReturnsPersistedRooms() {
        var rooms = roomService.getRooms();

        assertEquals(2, rooms.size());
        assertEquals(1, rooms.get(0).code());
        assertEquals("자유 대화실", rooms.get(0).title());
    }

    @Test
    void getRoomReturnsRoomDetail() {
        Long roomId = roomRepository.findAll().get(0).getId();

        var room = roomService.getRoom(roomId);

        assertEquals(roomId, room.id());
        assertEquals(1, room.code());
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

    @Test
    void createRoomPersistsNewRoom() {
        var created = roomService.createRoom(new RoomUpsertRequest(
                46,
                "게임좋아하는 사람",
                "게임 이야기를 나누는 방",
                "우리끼리 좋아",
                true
        ));

        assertNotNull(created.id());
        assertEquals(46, created.code());
        assertEquals(3, roomRepository.findAllByIsDeletedFalseOrderByCodeAsc().size());
    }

    @Test
    void updateRoomChangesPersistedFields() {
        Long roomId = roomRepository.findAllByIsDeletedFalseOrderByCodeAsc().get(0).getId();

        var updated = roomService.updateRoom(roomId, new RoomUpsertRequest(
                2,
                "자유로운 대화",
                "이름과 설명을 수정한 방",
                "평범함이 좋아",
                false
        ));

        assertEquals(2, updated.code());
        assertEquals("자유로운 대화", updated.title());
        assertFalse(updated.active());
    }

    @Test
    void deleteRoomMarksRoomDeleted() {
        Long roomId = roomRepository.findAllByIsDeletedFalseOrderByCodeAsc().get(0).getId();

        roomService.deleteRoom(roomId);

        assertEquals(1, roomService.getRooms().size());
        assertTrue(roomRepository.findById(roomId).orElseThrow().isDeleted());
    }
}
