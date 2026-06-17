package com.toyproject.t4lk.post;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "posts")
@CompoundIndex(name = "post_active_created_idx", def = "{'deleted': 1, 'createdAt': 1}")
public class Post {

    @Id
    private String id;

    private String parentId; // null이면 원글, 값이 있으면 해당 글의 답글
    private String title;
    private String authorName;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    protected Post() {
    }

    public Post(String parentId, String title, String authorName, String content) {
        Instant now = Instant.now();
        this.parentId = parentId;
        this.title = title;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = Instant.now();
    }
}
