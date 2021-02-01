package com.mountblue.blog.blogapplication.service;

import com.mountblue.blog.blogapplication.entity.Tags;
import java.util.List;

public interface TagsService {

    public List<Tags> findAll();
    public Tags findById(int theId);
    public void save(Tags theTags);
    public void deleteById(int theId);
}
