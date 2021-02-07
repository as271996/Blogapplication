package com.mountblue.blog.blogapplication.service.implementation;

import com.mountblue.blog.blogapplication.DAO.PostsRepository;
import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.entity.Tags;
import com.mountblue.blog.blogapplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostsServiceImpl implements PostsService {

    private PostsRepository thePostsRepository;

    @Autowired
    public PostsServiceImpl(PostsRepository thePostsRepository) {
        this.thePostsRepository = thePostsRepository;
    }

    @Override
    public List<Posts> findAll() {
        return thePostsRepository.findAll();
    }

    @Override
    public List<Posts> findAll(Sort sort) {
        return thePostsRepository.findAll(sort);
    }

    @Override
    public Page<Posts> findAll(Pageable pageable) {
        return thePostsRepository.findAll(pageable);
    }

    @Override
    public Posts findById(int theId) {
        Optional<Posts> result = thePostsRepository.findById(theId);
        Posts thePosts = null;
        if(result.isPresent()) {
            thePosts = result.get();
        } else {
            throw new RuntimeException("Did not find post id - " + theId);
        }
        return thePosts;
    }

    @Override
    public void save(Posts thePosts) {
        thePostsRepository.save(thePosts);
    }

    @Override
    public void deleteById(int theId) {
        thePostsRepository.deleteById(theId);
    }

    @Override
    public Page<Posts> search(String keyword, Pageable pageable) {
        keyword = keyword.strip();
        if (keyword == null) {
            throw new RuntimeException("Did not find any match for - " + keyword);
        } else if (keyword.isEmpty()) {
            throw new RuntimeException("Please avoid space search");
        }
        return (Page<Posts>) thePostsRepository.search(keyword, pageable);
    }

    @Override
    public List<Posts> searchInCurrentList(List<Posts> tempCurrentList, String keyword) {
        List<Posts> theCurrentSearchList = new ArrayList<>();
        if (tempCurrentList.size() <= 0) tempCurrentList = thePostsRepository.findAll();
        keyword = keyword.strip();
        if (keyword == null || keyword.isEmpty()) {
            return tempCurrentList;
        }
        for (Posts thePost: tempCurrentList) {
            String finalKeyword = keyword;
            if (thePost.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
            thePost.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
            thePost.getContent().toLowerCase().contains(keyword.toLowerCase()) ||
            thePost.getTagsList().stream().anyMatch(tag -> tag.getName().toLowerCase()
                    .contains(finalKeyword.toLowerCase()))){
                theCurrentSearchList.add(thePost);
            }
        }
        return theCurrentSearchList;
    }

    //###########################################################################################
    public Page<Posts> getCurrentPostList(Pageable pageable, List<Posts> theCurrentPostList) {
        int start = (int) pageable.getOffset();
        int end = ((start + pageable.getPageSize()) > theCurrentPostList.size()) ? theCurrentPostList.size() : (start + pageable.getPageSize());

        Page<Posts> page = new PageImpl<>(theCurrentPostList.subList(start, end), pageable, theCurrentPostList.size());
        return page;
    }

    @Override
    public List<Posts> sortPostsList(List<Posts> thePostsList, String sortBy, String direction) {

        if ("asc".equals(direction)) {
            if("author".equals(sortBy)) {
                Collections.sort(thePostsList, Comparator.comparing(Posts::getAuthor));
            }else{
                Collections.sort(thePostsList, Comparator.comparing(Posts::getPublishedAt));
            }
        } else {
            if("author".equals(sortBy)) {
                Collections.sort(thePostsList, Comparator.comparing(Posts::getAuthor).reversed());
            }else{
                Collections.sort(thePostsList, Comparator.comparing(Posts::getPublishedAt).reversed());
            }
        }
        return thePostsList;
    }

    @Override
    public List<Posts> findByAuthor(String theAuthor) {
        return thePostsRepository.findByAuthor(theAuthor);
    }

    @Override
    public Page<Posts> findByPublishedAt(Timestamp thePublishedAt, Pageable pageable) {
        return thePostsRepository.findByPublishedAt(thePublishedAt, pageable);
    }

    @Override
    public List<Posts> findByTag(String tags) {
        return thePostsRepository.findByTag(tags);
    }

    @Override
    public List<Posts> filterByDatePostList(List<Posts> thePostsList, Timestamp from, Timestamp to) {

        List<Posts> theFilteredPostList = new ArrayList<>();
        for (Posts post: thePostsList){
            if (post.getPublishedAt().after(from) && post.getPublishedAt().before(to)){
                theFilteredPostList.add(post);
            }
        }
        return theFilteredPostList;
    }
    //###########################################################################################
}
