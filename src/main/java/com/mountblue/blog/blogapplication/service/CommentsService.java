package com.mountblue.blog.blogapplication.service;

import com.mountblue.blog.blogapplication.entity.Comments;
import java.util.List;

public interface CommentsService {

    List<Comments> findAll();
    Comments findById(int theId);
    void save(Comments theComments);
    void deleteById(int theId);
}
