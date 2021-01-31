package com.mountblue.blog.blogapplication.service.implementation;

import com.mountblue.blog.blogapplication.DAO.TagsRepository;
import com.mountblue.blog.blogapplication.entity.Tags;
import com.mountblue.blog.blogapplication.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagsServiceImpl implements TagsService {

    private TagsRepository theTagsRepository;

    @Autowired
    public TagsServiceImpl(TagsRepository theTagsRepository) {
        this.theTagsRepository = theTagsRepository;
    }

    @Override
    public List<Tags> findAll() {
        return theTagsRepository.findAll();
    }

    @Override
    public Tags findById(int theId) {
        Optional<Tags> result = theTagsRepository.findById(theId);
        Tags theTags = null;
        if (result.isPresent()){
            theTags = result.get();
        }else {
            throw new RuntimeException("Did not find tag id - " + theId);
        }
        return theTags;
    }

    @Override
    public void save(Tags theTags) {
        theTagsRepository.save(theTags);
    }

    @Override
    public void deleteById(int theId) {
        theTagsRepository.deleteById(theId);
    }
}
