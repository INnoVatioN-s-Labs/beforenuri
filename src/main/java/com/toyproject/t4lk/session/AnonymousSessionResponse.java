package com.toyproject.t4lk.session;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "익명 세션 발급 응답")
public record AnonymousSessionResponse(
        @Schema(description = "임시 세션 토큰", example = "anon-token-1-myeongyereoun-panty")
        String sessionToken,
        @Schema(description = "화면에 표시할 익명 닉네임", example = "명예로운 팬티_192.168")
        String displayName
) {
}
