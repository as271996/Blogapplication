package com.mountblue.blog.blogapplication.controller;

import com.mountblue.blog.blogapplication.entity.Comments;
import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.service.CommentsService;
import com.mountblue.blog.blogapplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/blog")
public class BlogApplicationController {

    private PostsService thePostsService;
    private CommentsService theCommentsService;

    @Autowired
    public BlogApplicationController(PostsService thePostsService, CommentsService theCommentsService) {
        this.thePostsService = thePostsService;
        this.theCommentsService = theCommentsService;
    }

    // Add mapping for /bloglist
    @GetMapping("/bloglist")
    public String blogList(Model theModel){

        List<Posts> thePostList = thePostsService.findAll();
        theModel.addAttribute("blogList", thePostList);
        return "blog-list";
    }

    // Add mapping for /showFormForCreateBlog
    @GetMapping("/showFormForCreateBlog")
    public String showFormForCreateBlog(Model theModel){
        Posts thePosts = new Posts();
        theModel.addAttribute("posts", thePosts);
        return "create-blog-post";
    }

    // Add mapping for /showFullBlogPost
    @GetMapping("/showFullBlogPost")
    public String showFullBlogPost(@RequestParam("postId") int theId, Model theModel){
        Posts thePost = thePostsService.findById(theId);
        theModel.addAttribute("post", thePost);
        return "full-blog-post";
    }

    // Add mapping for /showFormForUpdate
    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("postId") int theId, Model theModel){
        Posts thePost = thePostsService.findById(theId);
        theModel.addAttribute("posts", thePost);
        return "create-blog-post";
    }

    // Add mapping for /save
    @PostMapping("/save")
    public String saveBlogPost(@ModelAttribute("posts") Posts thePosts){
        thePosts.setExcerpt(thePosts.getContent());
        thePosts.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        thePosts.setPublished(true);
        thePosts.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        thePosts.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        thePostsService.save(thePosts);
        return "redirect:/blog/bloglist";
    }

    // Add mapping for /delete
    @GetMapping("/delete")
    public String delete(@RequestParam("postId") int theId, Model theModel){
        thePostsService.deleteById(theId);
        return "redirect:/blog/bloglist";
    }

    // Add mapping for /showFormForComment
    @GetMapping("/showFormForComment")
    public String showFormForComment(@RequestParam("postId") int theId, Model theModel){
        Comments theComments = new Comments();
        theComments.setPostId(theId);
        theModel.addAttribute("comments", theComments);
        return "comment-form";
    }

    // Add mapping for /showFormForUpdateComment
    @GetMapping("/showFormForUpdateComment")
    public String showFormForUpdateComment(@RequestParam("commentId") int theId, Model theModel){
        Comments theComments = theCommentsService.findById(theId);
        theModel.addAttribute("comments", theComments);
        return "comment-form";

    }

    // Add mapping for /deleteComment
    @GetMapping("/deleteComment")
    public String deleteComment(@RequestParam("commentId") int theId, Model theModel){
        int postId = theCommentsService.findById(theId).getPostId();
        theCommentsService.deleteById(theId);
        return "redirect:/blog/showFullBlogPost?postId=" + postId;
    }

    // Add mapping for /saveComment
    @PostMapping("/saveComment")
    public String saveComment(@ModelAttribute("comments") Comments theComments){
        int theId = theComments.getPostId();
        Posts thePosts = thePostsService.findById(theId);
        theComments.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        theComments.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        thePosts.add(theComments);
        thePostsService.save(thePosts);
        return "redirect:/blog/showFullBlogPost?postId=" + theId;
    }
}






