package com.toyproject.t4lk.post;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 상세")
public record PostDetailResponse(
        @Schema(description = "게시글 id")
        String id,
        @Schema(description = "부모 글 id (원글이면 null)")
        String parentId,
        @Schema(description = "제목")
        String title,
        @Schema(description = "작성자")
        String authorName,
        @Schema(description = "본문")
        String content,
        @Schema(description = "작성 시각 (UTC, ISO-8601)")
        String createdAt
) {
}
