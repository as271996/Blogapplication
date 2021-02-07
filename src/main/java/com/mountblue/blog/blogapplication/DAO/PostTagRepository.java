package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Comments;
import com.mountblue.blog.blogapplication.entity.PostTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface PostTagRepository  extends JpaRepository<PostTags, Integer> {

    @Query("select pt from PostTags where pt.postId = ?1")
    public List<PostTags> findByPostId(int postId);

}
