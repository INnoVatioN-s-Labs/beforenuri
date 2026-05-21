package com.toyproject.t4lk.chat;

import com.toyproject.t4lk.room.Room;
import com.toyproject.t4lk.room.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceUnitTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getMessagesMapsRepositoryResults() {
        Room room = room(1L, 1, "자유 대화실");
        ChatMessage first = message("100", room.getId(), "명예로운 팬티_203.0", ChatMessageType.CHAT, "안녕하세요.");
        ChatMessage second = message("101", room.getId(), "시스템", ChatMessageType.SYSTEM, "공지입니다.");
        when(chatMessageRepository.findTop4ByRoomIdAndDeletedFalseOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(second, first));

        List<ChatMessageResponse> messages = chatService.getMessages(1L);

        assertEquals(2, messages.size());
        assertEquals("CHAT", messages.get(0).messageType());
        assertEquals("공지입니다.", messages.get(1).content());
    }

    @Test
    void createMessagePersistsAndReturnsResponse() {
        Room room = room(1L, 1, "자유 대화실");
        ChatMessage saved = message("100", room.getId(), "명예로운 팬티_192.168", ChatMessageType.CHAT, "실시간 메시지");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(saved);

        ChatMessageResponse created = chatService.createMessage(
                1L,
                new ChatMessageUpsertRequest("명예로운 팬티_192.168", ChatMessageType.CHAT, "실시간 메시지")
        );

        assertEquals("100", created.id());
        assertEquals("실시간 메시지", created.content());
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void updateMessageMutatesExistingEntity() {
        Room room = room(1L, 1, "자유 대화실");
        ChatMessage existing = message("100", room.getId(), "명예로운 팬티_192.168", ChatMessageType.CHAT, "기존 메시지");
        when(chatMessageRepository.findByIdAndRoomIdAndDeletedFalse("100", 1L)).thenReturn(Optional.of(existing));
        when(chatMessageRepository.save(existing)).thenReturn(existing);

        ChatMessageResponse updated = chatService.updateMessage(
                1L,
                "100",
                new ChatMessageUpsertRequest("용감한 고양이_192.168", ChatMessageType.SYSTEM, "수정된 메시지")
        );

        assertEquals("SYSTEM", updated.messageType());
        assertEquals("수정된 메시지", updated.content());
        assertEquals("용감한 고양이_192.168", existing.getSenderName());
    }

    @Test
    void updateMessageThrowsWhenMessageMissing() {
        when(chatMessageRepository.findByIdAndRoomIdAndDeletedFalse("999", 1L)).thenReturn(Optional.empty());

        assertThrows(ChatMessageNotFoundException.class, () ->
                chatService.updateMessage(
                        1L,
                        "999",
                        new ChatMessageUpsertRequest("용감한 고양이_192.168", ChatMessageType.CHAT, "수정")
                ));
    }

    @Test
    void deleteMessageMarksEntityDeleted() {
        Room room = room(1L, 1, "자유 대화실");
        ChatMessage existing = message("100", room.getId(), "명예로운 팬티_192.168", ChatMessageType.CHAT, "기존 메시지");
        when(chatMessageRepository.findByIdAndRoomIdAndDeletedFalse("100", 1L)).thenReturn(Optional.of(existing));
        when(chatMessageRepository.save(existing)).thenReturn(existing);

        chatService.deleteMessage(1L, "100");

        assertTrue(existing.isDeleted());
    }

    private Room room(Long id, Integer code, String title) {
        Room room = new Room(code, title, "설명", "평범함이 좋아", true);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }

    private ChatMessage message(String id, Long roomId, String senderName, ChatMessageType type, String content) {
        ChatMessage chatMessage = new ChatMessage(roomId, senderName, type, content);
        ReflectionTestUtils.setField(chatMessage, "id", id);
        ReflectionTestUtils.setField(chatMessage, "createdAt", LocalDateTime.of(2026, 5, 7, 19, 30));
        ReflectionTestUtils.setField(chatMessage, "updatedAt", LocalDateTime.of(2026, 5, 7, 19, 30));
        ReflectionTestUtils.setField(chatMessage, "deleted", false);
        return chatMessage;
    }
}
