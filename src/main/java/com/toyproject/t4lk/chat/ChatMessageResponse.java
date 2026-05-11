package com.toyproject.t4lk.chat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 메시지 응답")
public record ChatMessageResponse(
        @Schema(description = "메시지 번호", example = "1001")
        String id,
        @Schema(description = "채팅방 번호", example = "1")
        Long roomId,
        @Schema(description = "보낸 사람 표시 이름", example = "명예로운 팬티_192.168")
        String senderName,
        @Schema(description = "메시지 유형", example = "CHAT")
        String messageType,
        @Schema(description = "메시지 내용", example = "안녕하세요. 반갑습니다.")
        String content,
        @Schema(description = "메시지 생성 시각", example = "2026-05-05T19:30:00")
        String createdAt
) {
}
