package com.toyproject.t4lk.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @NotBlank
        @Schema(description = "로그인 아이디", example = "gildong")
        String username,

        @NotBlank
        @Schema(description = "비밀번호", example = "password1234")
        String password
) {
}
