package com.toyproject.t4lk.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9_]{4,20}$", message = "아이디는 영문/숫자/_ 4~20자여야 합니다.")
        @Schema(description = "로그인 아이디 (영문/숫자/_, 4~20자)", example = "gildong")
        String username,

        @NotBlank
        @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
        @Schema(description = "비밀번호 (8~64자)", example = "password1234")
        String password,

        @NotBlank
        @Size(min = 1, max = 20, message = "닉네임은 1~20자여야 합니다.")
        @Schema(description = "대화/게시판에 표시할 고정 닉네임", example = "길동이")
        String displayName
) {
}
