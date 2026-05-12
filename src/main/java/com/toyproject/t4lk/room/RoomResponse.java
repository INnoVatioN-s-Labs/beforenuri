package com.toyproject.t4lk.room;

import io.swagger.v3.oas.annotations.media.Schema;

public record RoomResponse(
        @Schema(description = "채팅방 번호", example = "1")
        Long id,
        @Schema(description = "프론트에서 사용하는 방 코드", example = "21")
        Integer code,
        @Schema(description = "채팅방 제목", example = "자유 대화실")
        String title,
        @Schema(description = "채팅방 설명", example = "누구나 편하게 이야기하는 기본 방")
        String description,
        @Schema(description = "채팅방 분류", example = "지역별 대화실")
        String category,
        @Schema(description = "채팅방 활성 여부", example = "true")
        boolean active
) {
}
