package com.toyproject.t4lk.chat;

import java.util.List;

import com.toyproject.t4lk.room.RoomService;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RoomService roomService;

    public ChatService(ChatMessageRepository chatMessageRepository, RoomService roomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.roomService = roomService;
    }

    public List<ChatMessageResponse> getMessages(Long roomId) {
        roomService.validateRoomExists(roomId);
        return chatMessageRepository.findAllByRoomIdAndDeletedFalseOrderByCreatedAtAsc(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ChatMessageResponse createMessage(Long roomId, ChatMessageUpsertRequest request) {
        roomService.validateRoomExists(roomId);
        ChatMessage chatMessage = chatMessageRepository.save(new ChatMessage(
                roomId,
                request.senderName(),
                request.messageType(),
                request.content()
        ));
        return toResponse(chatMessage);
    }

    public ChatMessageResponse updateMessage(Long roomId, String messageId, ChatMessageUpsertRequest request) {
        roomService.validateRoomExists(roomId);
        ChatMessage chatMessage = getMessageEntity(roomId, messageId);
        chatMessage.update(request.senderName(), request.messageType(), request.content());
        return toResponse(chatMessageRepository.save(chatMessage));
    }

    public void deleteMessage(Long roomId, String messageId) {
        roomService.validateRoomExists(roomId);
        ChatMessage chatMessage = getMessageEntity(roomId, messageId);
        chatMessage.delete();
        chatMessageRepository.save(chatMessage);
    }

    private ChatMessage getMessageEntity(Long roomId, String messageId) {
        return chatMessageRepository.findByIdAndRoomIdAndDeletedFalse(messageId, roomId)
                .orElseThrow(() -> new ChatMessageNotFoundException(roomId, messageId));
    }

    private ChatMessageResponse toResponse(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                chatMessage.getSenderName(),
                chatMessage.getMessageType().name(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt().toString()
        );
    }
}
