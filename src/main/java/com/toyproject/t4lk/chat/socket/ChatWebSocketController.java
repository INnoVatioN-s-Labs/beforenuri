package com.toyproject.t4lk.chat.socket;

import com.toyproject.t4lk.chat.ChatMessageType;
import com.toyproject.t4lk.chat.ChatMessageUpsertRequest;
import com.toyproject.t4lk.chat.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/rooms/{roomId}/messages")
    public void sendMessage(@DestinationVariable Long roomId, @Payload @Valid ChatRealtimeRequest request) {
        var savedMessage = chatService.createMessage(roomId, new ChatMessageUpsertRequest(
                request.senderName(),
                ChatMessageType.CHAT,
                request.content()
        ));
        messagingTemplate.convertAndSend(
                "/topic/rooms/" + roomId,
                ChatSocketMessageResponse.from(savedMessage)
        );
    }
}
