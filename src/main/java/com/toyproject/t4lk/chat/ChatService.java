package com.toyproject.t4lk.chat;

import java.util.List;

import com.toyproject.t4lk.room.Room;
import com.toyproject.t4lk.room.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RoomService roomService;

    public ChatService(ChatMessageRepository chatMessageRepository, RoomService roomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.roomService = roomService;
    }

    public List<ChatMessageResponse> getMessages(Long roomId) {
        Room room = roomService.getRoomEntity(roomId);
        return chatMessageRepository.findAllByRoom_IdAndIsDeletedFalseOrderByCreatedAtAsc(room.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ChatMessageResponse toResponse(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoom().getId(),
                chatMessage.getSenderName(),
                chatMessage.getMessageType().name(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt().toString()
        );
    }
}
