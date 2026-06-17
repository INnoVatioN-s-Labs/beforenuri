package com.toyproject.t4lk.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getPostsFlattensTreeWithDepth() {
        // 1(원글) - 2(답글) - 3(답답글), 그리고 4(원글)
        Post root = post("1", null, "원글", t(0));
        Post child = post("2", "1", "답글", t(1));
        Post grandchild = post("3", "2", "답답글", t(2));
        Post root2 = post("4", null, "원글2", t(3));
        when(postRepository.findAllByDeletedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(root, child, grandchild, root2));

        List<PostListItemResponse> result = postService.getPosts();

        assertEquals(List.of("1", "2", "3", "4"), result.stream().map(PostListItemResponse::id).toList());
        assertEquals(List.of(0, 1, 2, 0), result.stream().map(PostListItemResponse::depth).toList());
    }

    @Test
    void createReplyUnderMissingParentThrows() {
        when(postRepository.findByIdAndDeletedFalse("999")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () ->
                postService.createPost(new PostUpsertRequest("제목", "내용", "999"), "작성자"));
    }

    @Test
    void createRootPostPersists() {
        Post saved = post("10", null, "새 글", t(0));
        when(postRepository.save(any(Post.class))).thenReturn(saved);

        PostDetailResponse created = postService.createPost(new PostUpsertRequest("새 글", "내용", null), "작성자");

        assertEquals("10", created.id());
        verify(postRepository).save(any(Post.class));
    }

    private Post post(String id, String parentId, String title, Instant createdAt) {
        Post post = new Post(parentId, title, "작성자", "내용");
        ReflectionTestUtils.setField(post, "id", id);
        ReflectionTestUtils.setField(post, "createdAt", createdAt);
        return post;
    }

    private Instant t(int seconds) {
        return Instant.parse("2026-05-07T00:00:0" + seconds + "Z");
    }
}
