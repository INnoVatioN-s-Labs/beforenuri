package com.toyproject.t4lk.post;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 목록 항목 (트리 평탄화, depth로 들여쓰기)")
public record PostListItemResponse(
        @Schema(description = "게시글 id", example = "665f...")
        String id,
        @Schema(description = "부모 글 id (원글이면 null)")
        String parentId,
        @Schema(description = "답글 깊이 (0=원글)", example = "0")
        int depth,
        @Schema(description = "제목", example = "처음 가입했어요")
        String title,
        @Schema(description = "작성자", example = "초보 사용자_192.168")
        String authorName,
        @Schema(description = "작성 시각 (UTC, ISO-8601)", example = "2026-05-07T12:15:00Z")
        String createdAt
) {
}
