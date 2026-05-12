package com.toyproject.t4lk.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "채팅 메시지 생성/수정 요청")
public record ChatMessageUpsertRequest(
        @NotBlank
        @Size(max = 120)
        @Schema(description = "표시할 발신자 이름", example = "명예로운 팬티_192.168")
        String senderName,
        @NotNull
        @Schema(description = "메시지 타입", example = "CHAT")
        ChatMessageType messageType,
        @NotBlank
        @Size(max = 500)
        @Schema(description = "메시지 내용", example = "안녕하세요.")
        String content
) {
}
