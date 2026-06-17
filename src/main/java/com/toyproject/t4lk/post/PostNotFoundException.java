package com.toyproject.t4lk.post;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String postId) {
        super("존재하지 않는 게시글입니다. postId=" + postId);
    }
}
