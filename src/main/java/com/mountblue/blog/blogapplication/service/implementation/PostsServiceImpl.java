package com.mountblue.blog.blogapplication.service.implementation;

import com.mountblue.blog.blogapplication.DAO.PostsRepository;
import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
