package com.toyproject.t4lk.chat.socket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "실시간 채팅 전송 요청")
public record ChatRealtimeRequest(
        @NotBlank
        @Size(max = 120)
        @Schema(description = "표시할 발신자 이름", example = "명예로운 팬티_192.168")
        String senderName,
        @NotBlank
        @Size(max = 500)
        @Schema(description = "채팅 메시지", example = "실시간으로 보냅니다.")
        String content
) {
}
