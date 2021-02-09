package com.mountblue.blog.blogapplication.service.implementation;

import com.mountblue.blog.blogapplication.DAO.CommentsRepository;
import com.mountblue.blog.blogapplication.entity.Comments;
import com.mountblue.blog.blogapplication.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentsServiceImpl implements CommentsService {

    private CommentsRepository theCommentsRepository;

    @Autowired
    public CommentsServiceImpl(CommentsRepository theCommentsRepository) {
        this.theCommentsRepository = theCommentsRepository;
    }

    @Override
    public List<Comments> findAll() {
        return theCommentsRepository.findAll();
    }

    @Override
    public Comments findById(int theId) {
        Optional<Comments> result = theCommentsRepository.findById(theId);
        Comments theComments = null;
        if (result.isPresent()) {
            theComments = result.get();
        } else {
            throw new RuntimeException("Did not find comment id - " + theId);
        }
        return theComments;
    }

    @Override
    public void save(Comments theComments) {
        theCommentsRepository.save(theComments);
    }

    @Override
    public void deleteById(int theId) {
        theCommentsRepository.deleteById(theId);
    }
}
