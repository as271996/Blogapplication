package com.mountblue.blog.blogapplication.controller;

import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.service.CommentsService;
import com.mountblue.blog.blogapplication.service.PostsService;
import com.mountblue.blog.blogapplication.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sort")
public class BlogSorting {

    private PostsService thePostsService;
    private CommentsService theCommentsService;
    private TagsService theTagsService;

    @Autowired
    public BlogSorting(PostsService thePostsService, CommentsService theCommentsService, TagsService theTagsService) {
        this.thePostsService = thePostsService;
        this.theCommentsService = theCommentsService;
        this.theTagsService = theTagsService;
    }

}
