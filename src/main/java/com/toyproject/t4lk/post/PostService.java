package com.toyproject.t4lk.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /** 전체 글을 parentId 기준 트리로 묶어 DFS 순서로 평탄화하고 depth를 부여한다. */
    public List<PostListItemResponse> getPosts() {
        List<Post> all = postRepository.findAllByDeletedFalseOrderByCreatedAtAsc();

        Map<String, List<Post>> childrenByParent = new HashMap<>();
        List<Post> roots = new ArrayList<>();
        for (Post post : all) {
            if (post.getParentId() == null) {
                roots.add(post);
            } else {
                childrenByParent.computeIfAbsent(post.getParentId(), key -> new ArrayList<>()).add(post);
            }
        }

        List<PostListItemResponse> result = new ArrayList<>();
        for (Post root : roots) {
            appendTree(root, 0, childrenByParent, result);
        }
        return result;
    }

    private void appendTree(Post post, int depth, Map<String, List<Post>> childrenByParent, List<PostListItemResponse> out) {
        out.add(toListItem(post, depth));
        for (Post child : childrenByParent.getOrDefault(post.getId(), List.of())) {
            appendTree(child, depth + 1, childrenByParent, out);
        }
    }

    public PostDetailResponse getPost(String id) {
        Post post = postRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return toDetail(post);
    }

    public PostDetailResponse createPost(PostUpsertRequest request, String authorName) {
        if (request.parentId() != null) {
            // 답글 대상 글이 존재하는지 검증한다.
            postRepository.findByIdAndDeletedFalse(request.parentId())
                    .orElseThrow(() -> new PostNotFoundException(request.parentId()));
        }
        Post saved = postRepository.save(new Post(
                request.parentId(),
                request.title(),
                authorName,
                request.content()
        ));
        return toDetail(saved);
    }

    private PostListItemResponse toListItem(Post post, int depth) {
        return new PostListItemResponse(
                post.getId(),
                post.getParentId(),
                depth,
                post.getTitle(),
                post.getAuthorName(),
                post.getCreatedAt().toString()
        );
    }

    private PostDetailResponse toDetail(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getParentId(),
                post.getTitle(),
                post.getAuthorName(),
                post.getContent(),
                post.getCreatedAt().toString()
        );
    }
}
