package com.toyproject.t4lk.chat.socket;

import com.toyproject.t4lk.chat.ChatMessageResponse;
import com.toyproject.t4lk.chat.ChatMessageType;
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
        @Schema(description = "생성 시각 (UTC, ISO-8601)", example = "2026-05-07T12:15:00Z")
        String createdAt,
        @Schema(description = "현재 방 접속자 수 (입장/퇴장 알림에만 포함, 일반 메시지는 null)", example = "3")
        Integer occupantCount
) {
    public static ChatSocketMessageResponse from(ChatMessageResponse response) {
        return new ChatSocketMessageResponse(
                response.messageType(),
                response.id(),
                response.roomId(),
                response.senderName(),
                response.content(),
                response.createdAt(),
                null
        );
    }

    /** 입장/퇴장 등 서버가 생성하는 시스템 알림용. 접속자 수를 함께 싣는다. */
    public static ChatSocketMessageResponse system(Long roomId, String content, int occupantCount, String createdAt) {
        return new ChatSocketMessageResponse(
                ChatMessageType.SYSTEM.name(),
                null,
                roomId,
                "SYSTEM",
                content,
                createdAt,
                occupantCount
        );
    }
}
