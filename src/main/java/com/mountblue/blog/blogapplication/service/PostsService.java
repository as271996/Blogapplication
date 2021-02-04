package com.mountblue.blog.blogapplication.service;

import com.mountblue.blog.blogapplication.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PostsService {

    public List<Posts> findAll();
    public List<Posts> findAll(Sort sort);
    public Page<Posts> findAll(Pageable pageable);
    public Posts findById(int theId);
    public void save(Posts thePosts);
    public void deleteById(int theId);

    public Page<Posts> search(String keyword, Pageable pageable);
}
