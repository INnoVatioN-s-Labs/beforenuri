package com.toyproject.t4lk.chat;

import com.toyproject.t4lk.room.Room;
import com.toyproject.t4lk.room.RoomRepository;
import com.toyproject.t4lk.room.RoomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        chatMessageRepository.deleteAll();
        roomRepository.deleteAll();

        Room room = roomRepository.save(new Room("자유 대화실", "누구나 편하게 이야기하는 기본 방", true));
        chatMessageRepository.save(new ChatMessage(room, "명예로운 팬티_192.168", ChatMessageType.CHAT, "안녕하세요."));
        chatMessageRepository.save(new ChatMessage(room, "용감한 고양이_192.168", ChatMessageType.SYSTEM, "시스템 공지입니다."));
    }

    @Test
    void getMessagesReturnsPersistedMessages() {
        Long roomId = roomRepository.findAll().get(0).getId();

        var messages = chatService.getMessages(roomId);

        assertEquals(2, messages.size());
        assertEquals("명예로운 팬티_192.168", messages.get(0).senderName());
        assertEquals("CHAT", messages.get(0).messageType());
    }

    @Test
    void getMessagesThrowsWhenRoomMissing() {
        assertThrows(RoomNotFoundException.class, () -> chatService.getMessages(999L));
    }
}
