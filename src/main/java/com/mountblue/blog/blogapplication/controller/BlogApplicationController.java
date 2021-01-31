package com.mountblue.blog.blogapplication.controller;

import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogApplicationController {

    private PostsService thePostsService;

    @Autowired
    public BlogApplicationController(PostsService thePostsService) {
        this.thePostsService = thePostsService;
    }

    @GetMapping("/posts")
    public String blogList(Model theModel){
        List<Posts> thePostList = thePostsService.findAll();
        theModel.addAttribute("postList", thePostList);
        return "blog-list";
    }
}
