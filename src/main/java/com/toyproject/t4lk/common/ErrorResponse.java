package com.toyproject.t4lk.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 에러 응답")
public record ErrorResponse(
        @Schema(description = "에러 코드", example = "ROOM_NOT_FOUND")
        String code,
        @Schema(description = "에러 메시지", example = "존재하지 않는 채팅방입니다.")
        String message
) {
}
