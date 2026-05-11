package com.toyproject.t4lk.room;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceUnitTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void getRoomsMapsRepositoryResults() {
        Room first = room(1L, 1, "자유 대화실", "기본 방", "평범함이 좋아", true);
        Room second = room(2L, 21, "서울특별시", "지역 방", "지역별 대화실", true);
        when(roomRepository.findAllByIsDeletedFalseOrderByCodeAsc()).thenReturn(List.of(first, second));

        List<RoomResponse> rooms = roomService.getRooms();

        assertEquals(2, rooms.size());
        assertEquals(1, rooms.get(0).code());
        assertEquals("서울특별시", rooms.get(1).title());
    }

    @Test
    void getRoomThrowsWhenRepositoryMisses() {
        when(roomRepository.findByIdAndIsDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoom(999L));
    }

    @Test
    void createRoomSavesNewEntity() {
        RoomUpsertRequest request = new RoomUpsertRequest(46, "게임좋아하는 사람", "게임 방", "우리끼리 좋아", true);
        Room saved = room(3L, 46, "게임좋아하는 사람", "게임 방", "우리끼리 좋아", true);
        when(roomRepository.findByCodeAndIsDeletedFalse(46)).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomResponse created = roomService.createRoom(request);

        assertEquals(3L, created.id());
        assertEquals(46, created.code());
        assertEquals("우리끼리 좋아", created.category());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void createRoomThrowsWhenCodeAlreadyExists() {
        when(roomRepository.findByCodeAndIsDeletedFalse(21))
                .thenReturn(Optional.of(room(1L, 21, "서울특별시", "지역 방", "지역별 대화실", true)));

        assertThrows(RoomCodeAlreadyExistsException.class, () ->
                roomService.createRoom(new RoomUpsertRequest(21, "부산/제주", "설명", "지역별 대화실", true)));

        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void updateRoomMutatesExistingEntity() {
        Room existing = room(1L, 1, "자유 대화실", "기본 방", "평범함이 좋아", true);
        when(roomRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));
        when(roomRepository.findByCodeAndIsDeletedFalse(2)).thenReturn(Optional.empty());

        RoomResponse updated = roomService.updateRoom(
                1L,
                new RoomUpsertRequest(2, "자유로운 대화", "수정된 설명", "평범함이 좋아", false)
        );

        assertEquals(2, updated.code());
        assertEquals("자유로운 대화", updated.title());
        assertFalse(updated.active());
    }

    @Test
    void deleteRoomMarksEntityDeleted() {
        Room existing = room(1L, 1, "자유 대화실", "기본 방", "평범함이 좋아", true);
        when(roomRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existing));

        roomService.deleteRoom(1L);

        assertTrue(existing.isDeleted());
    }

    private Room room(Long id, Integer code, String title, String description, String category, boolean active) {
        Room room = new Room(code, title, description, category, active);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }
}
