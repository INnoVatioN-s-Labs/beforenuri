package com.toyproject.t4lk.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "게시글/답글 작성 요청 (작성자는 세션 토큰으로 서버가 식별)")
public record PostUpsertRequest(
        @NotBlank
        @Size(max = 100)
        @Schema(description = "제목", example = "처음 가입했어요")
        String title,
        @NotBlank
        @Size(max = 2000)
        @Schema(description = "본문", example = "잘 부탁드립니다.")
        String content,
        @Schema(description = "답글 대상 글 id (원글 작성이면 null)")
        String parentId
) {
}
