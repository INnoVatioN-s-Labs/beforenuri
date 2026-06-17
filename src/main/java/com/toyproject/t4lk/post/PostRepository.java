package com.toyproject.t4lk.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findAllByDeletedFalseOrderByCreatedAtAsc();

    Optional<Post> findByIdAndDeletedFalse(String id);
}
