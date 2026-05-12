package com.toyproject.t4lk.chat.socket;

import com.toyproject.t4lk.chat.ChatMessageResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "실시간 채팅 브로드캐스트 응답")
public record ChatSocketMessageResponse(
        @Schema(description = "이벤트 타입", example = "CHAT")
        String type,
        @Schema(description = "메시지 번호", example = "1001")
        String messageId,
        @Schema(description = "채팅방 번호", example = "1")
        Long roomId,
        @Schema(description = "표시할 발신자 이름", example = "명예로운 팬티_192.168")
        String senderName,
        @Schema(description = "메시지 내용", example = "안녕하세요.")
        String content,
        @Schema(description = "생성 시각", example = "2026-05-07T21:15:00")
        String createdAt
) {
    public static ChatSocketMessageResponse from(ChatMessageResponse response) {
        return new ChatSocketMessageResponse(
                response.messageType(),
                response.id(),
                response.roomId(),
                response.senderName(),
                response.content(),
                response.createdAt()
        );
    }
}
