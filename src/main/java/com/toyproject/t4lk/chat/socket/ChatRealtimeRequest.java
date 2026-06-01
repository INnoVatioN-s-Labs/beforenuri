package com.toyproject.t4lk.chat.socket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "실시간 채팅 전송 요청 (발신자는 세션 토큰으로 서버가 식별)")
public record ChatRealtimeRequest(
        @NotBlank
        @Size(max = 500)
        @Schema(description = "채팅 메시지", example = "실시간으로 보냅니다.")
        String content
) {
}
