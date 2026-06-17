package com.toyproject.t4lk.post;

import java.util.List;

import com.toyproject.t4lk.common.ErrorResponse;
import com.toyproject.t4lk.session.IdentityResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "자유게시판 글/답글 API (PC통신 스레드 형식)")
public class PostController {

    private final PostService postService;
    private final IdentityResolver identityResolver;

    public PostController(PostService postService, IdentityResolver identityResolver) {
        this.postService = postService;
        this.identityResolver = identityResolver;
    }

    @GetMapping
    @Operation(
            summary = "게시글 목록 조회",
            description = "원글과 답글을 트리(DFS) 순서로 평탄화해 반환합니다. depth로 들여쓰기 단계를 표현합니다."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PostListItemResponse.class))
            )
    )
    public List<PostListItemResponse> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회")
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 게시글입니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public PostDetailResponse getPost(@PathVariable String id) {
        return postService.getPost(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "게시글/답글 작성",
            description = "sessionToken 헤더로 작성자 신원을 고정합니다. parentId가 있으면 답글로 등록됩니다."
    )
    public PostDetailResponse createPost(
            @RequestHeader("sessionToken") String sessionToken,
            @Valid @RequestBody PostUpsertRequest request
    ) {
        String authorName = identityResolver.resolveDisplayName(sessionToken);
        return postService.createPost(request, authorName);
    }
}
