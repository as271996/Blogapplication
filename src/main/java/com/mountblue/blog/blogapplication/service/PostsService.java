package com.mountblue.blog.blogapplication.service;

import com.mountblue.blog.blogapplication.entity.Posts;
import java.util.List;

public interface PostsService {

    public List<Posts> findAll();
    public Posts findById(int theId);
    public void save(Posts thePosts);
    public void deleteById(int theId);
}
