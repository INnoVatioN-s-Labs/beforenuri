package com.toyproject.t4lk.chat.socket;

import java.security.Principal;

import com.toyproject.t4lk.chat.ChatMessageType;
import com.toyproject.t4lk.chat.ChatMessageUpsertRequest;
import com.toyproject.t4lk.chat.ChatService;
import com.toyproject.t4lk.session.InvalidSessionException;
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
    public void sendMessage(@DestinationVariable Long roomId, @Payload @Valid ChatRealtimeRequest request, Principal principal) {
        if (principal == null) {
            // CONNECT 인터셉터가 Principal을 바인딩하지 못한 경우(토큰 누락 등)의 방어 코드.
            throw new InvalidSessionException();
        }
        var savedMessage = chatService.createMessage(roomId, new ChatMessageUpsertRequest(
                principal.getName(),
                ChatMessageType.CHAT,
                request.content()
        ));
        messagingTemplate.convertAndSend(
                "/topic/rooms/" + roomId,
                ChatSocketMessageResponse.from(savedMessage)
        );
    }
}
