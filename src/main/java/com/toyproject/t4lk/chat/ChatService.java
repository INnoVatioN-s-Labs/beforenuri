package com.toyproject.t4lk.chat;

import java.util.List;

import com.toyproject.t4lk.room.Room;
import com.toyproject.t4lk.room.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RoomService roomService;

    public ChatService(ChatMessageRepository chatMessageRepository, RoomService roomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.roomService = roomService;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long roomId) {
        Room room = roomService.getRoomEntity(roomId);
        return chatMessageRepository.findAllByRoom_IdAndIsDeletedFalseOrderByCreatedAtAsc(room.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ChatMessageResponse createMessage(Long roomId, ChatMessageUpsertRequest request) {
        Room room = roomService.getRoomEntity(roomId);
        ChatMessage chatMessage = chatMessageRepository.save(new ChatMessage(
                room,
                request.senderName(),
                request.messageType(),
                request.content()
        ));
        return toResponse(chatMessage);
    }

    public ChatMessageResponse updateMessage(Long roomId, Long messageId, ChatMessageUpsertRequest request) {
        roomService.getRoomEntity(roomId);
        ChatMessage chatMessage = getMessageEntity(roomId, messageId);
        chatMessage.update(request.senderName(), request.messageType(), request.content());
        return toResponse(chatMessage);
    }

    public void deleteMessage(Long roomId, Long messageId) {
        roomService.getRoomEntity(roomId);
        ChatMessage chatMessage = getMessageEntity(roomId, messageId);
        chatMessage.delete();
    }

    private ChatMessage getMessageEntity(Long roomId, Long messageId) {
        return chatMessageRepository.findByIdAndRoom_IdAndIsDeletedFalse(messageId, roomId)
                .orElseThrow(() -> new ChatMessageNotFoundException(roomId, messageId));
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
