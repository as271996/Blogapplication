package com.mountblue.blog.blogapplication.service;

import com.mountblue.blog.blogapplication.entity.Tags;
import java.util.List;

public interface TagsService {

    List<Tags> findAll();
    Tags findById(int theId);
    void save(Tags theTags);
    void deleteById(int theId);
}
