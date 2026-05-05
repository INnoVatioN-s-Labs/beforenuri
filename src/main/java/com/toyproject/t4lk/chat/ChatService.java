package com.toyproject.t4lk.chat;

import java.util.List;
import java.util.Map;

import com.toyproject.t4lk.room.RoomService;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Map<Long, List<ChatMessageResponse>> MESSAGES = Map.of(
            1L, List.of(
                    new ChatMessageResponse(1001L, 1L, "명예로운 팬티_192.168", "CHAT", "안녕하세요. 반갑습니다.", "2026-05-05T19:30:00"),
                    new ChatMessageResponse(1002L, 1L, "용감한 고양이_192.168", "CHAT", "여기 분위기 좋네요.", "2026-05-05T19:31:00")
            ),
            2L, List.of(
                    new ChatMessageResponse(2001L, 2L, "조용한 모뎀_192.168", "CHAT", "심야 잡담방 테스트 중입니다.", "2026-05-05T23:10:00")
            ),
            3L, List.of(
                    new ChatMessageResponse(3001L, 3L, "날카로운 사용자_192.168", "SYSTEM", "현재 비활성화된 방입니다.", "2026-05-05T22:00:00")
            )
    );

    private final RoomService roomService;

    public ChatService(RoomService roomService) {
        this.roomService = roomService;
    }

    public List<ChatMessageResponse> getMessages(Long roomId) {
        roomService.getRoom(roomId);
        return MESSAGES.getOrDefault(roomId, List.of());
    }
}
