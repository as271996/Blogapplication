package com.mountblue.blog.blogapplication.service;

import com.mountblue.blog.blogapplication.entity.PostTags;
import com.mountblue.blog.blogapplication.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import java.util.List;

public interface PostsService {

    public List<Posts> findAll();
    public Posts findById(int theId);
    public void save(Posts thePosts);
    public void deleteById(int theId);

    public List<Posts> searchInCurrentList(List<Posts> tempCurrentList, String keyword);
    public Page<Posts> getCurrentPostList(Pageable pageable,List<Posts> theCurrentPostList);
    public Model pagingCalculation(Model theModel, int totalNumberOfBlog, int pageNo, int pageSize);
    public List<Posts> sortPostsList(List<Posts> thePostsList,String sortBy, String direction);

    public List<Posts> findByAuthor(String theAuthor);
    public List<Posts> findByTag(String tags);
    public List<Posts> filterByDatePostList(List<Posts> thePostsList, Timestamp from, Timestamp to);
}
