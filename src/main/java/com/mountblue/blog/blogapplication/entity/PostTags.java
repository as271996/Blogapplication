package com.mountblue.blog.blogapplication.entity;

import java.sql.Timestamp;

/*@Entity
@Table(name = "post_tags")*/
public class PostTags {

    //@Column(name = "post_id")
    private int postId;

    //@Column(name = "tag_id")
    private int tagId;

    //@Column(name = "created_at")
    private Timestamp createdAt;

    //@Column(name = "updated_at")
    private Timestamp updatedAt;

    public PostTags() {
    }

    public int getPostId() {
        return postId;
    }

    public int getTagId() {
        return tagId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
