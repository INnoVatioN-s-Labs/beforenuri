package com.toyproject.t4lk.member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 토큰 응답")
public record TokenResponse(
        @Schema(description = "액세스 토큰 (JWT, 1시간). 세션 토큰 자리에 그대로 사용")
        String accessToken,
        @Schema(description = "리프레시 토큰 (7일, 갱신 시 회전)")
        String refreshToken,
        @Schema(description = "고정 닉네임", example = "길동이")
        String displayName
) {
}
