package com.toyproject.t4lk.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 갱신 요청")
public record RefreshRequest(
        @NotBlank
        @Schema(description = "리프레시 토큰")
        String refreshToken
) {
}
