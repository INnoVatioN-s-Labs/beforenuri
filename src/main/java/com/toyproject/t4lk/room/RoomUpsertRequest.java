package com.toyproject.t4lk.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "채팅방 생성/수정 요청")
public record RoomUpsertRequest(
        @NotNull
        @Schema(description = "프론트에서 사용하는 방 코드", example = "21")
        Integer code,
        @NotBlank
        @Size(max = 100)
        @Schema(description = "채팅방 제목", example = "서울특별시")
        String title,
        @NotBlank
        @Size(max = 255)
        @Schema(description = "채팅방 설명", example = "서울 지역 이용자들이 모이는 대화방")
        String description,
        @NotBlank
        @Size(max = 60)
        @Schema(description = "채팅방 분류", example = "지역별 대화실")
        String category,
        @NotNull
        @Schema(description = "채팅방 활성 여부", example = "true")
        Boolean active
) {
}
