package com.toyproject.t4lk.chat.socket;

import java.security.Principal;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toyproject.t4lk.chat.socket.PresenceService.PresenceChange;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * STOMP 구독/연결 종료 이벤트로 대화실 입장·퇴장을 감지해, 해당 방에 시스템 알림과
 * 현재 접속자 수를 브로드캐스트한다. 입장은 /topic/rooms/{id} 구독 시점으로 판단한다.
 */
@Component
public class ChatPresenceEventListener {

    private static final Pattern ROOM_TOPIC = Pattern.compile("/topic/rooms/(\\d+)");

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatPresenceEventListener(PresenceService presenceService, SimpMessagingTemplate messagingTemplate) {
        this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }
        Matcher matcher = ROOM_TOPIC.matcher(destination);
        if (!matcher.matches()) {
            return;
        }
        Long roomId = Long.parseLong(matcher.group(1));
        String displayName = resolveName(event.getUser());
        PresenceChange change = presenceService.enter(roomId, accessor.getSessionId(), displayName);
        broadcast(change.roomId(), displayName + " 님이 입장하셨습니다.", change.occupantCount());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        PresenceChange change = presenceService.leave(accessor.getSessionId());
        if (change == null || change.displayName() == null) {
            return;
        }
        broadcast(change.roomId(), change.displayName() + " 님이 퇴장하셨습니다.", change.occupantCount());
    }

    private void broadcast(Long roomId, String content, int occupantCount) {
        messagingTemplate.convertAndSend(
                "/topic/rooms/" + roomId,
                ChatSocketMessageResponse.system(roomId, content, occupantCount, Instant.now().toString())
        );
    }

    private String resolveName(Principal principal) {
        return principal != null ? principal.getName() : "익명";
    }
}
