package com.mountblue.blog.blogapplication.controller;

import com.mountblue.blog.blogapplication.entity.Comments;
import com.mountblue.blog.blogapplication.entity.CurrentPostList;
import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.service.CommentsService;
import com.mountblue.blog.blogapplication.service.PostsService;
import com.mountblue.blog.blogapplication.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@SessionAttributes({"theCurrentPostList"})
@RequestMapping("/blog")
public class BlogApplicationController {

    private PostsService thePostsService;
    private CommentsService theCommentsService;
    private TagsService theTagsService;

    private static final int pageSize = 10;

    @ModelAttribute("theCurrentPostList")
    public CurrentPostList setCurrentPostList(){
        return new CurrentPostList();
    }

    @Autowired
    public BlogApplicationController(PostsService thePostsService, CommentsService theCommentsService,
                                     TagsService theTagsService) {
        this.thePostsService = thePostsService;
        this.theCommentsService = theCommentsService;
        this.theTagsService = theTagsService;
    }
//#####################################################################################################################
//                                             PRINTING ALL BLOG POSTS HERE
//#####################################################################################################################
    // Add mapping for /bloglist
    @GetMapping(value = {"/bloglist"})
    public String blogList(@RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                           @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                           Model theModel){

        Pageable paging = PageRequest.of(pageNo, pageSize);
        theCurrentPostList.setTempCurrentPostsList(thePostsService.findAll());
        Page<Posts> thePostPages = thePostsService.getCurrentPostList(paging,theCurrentPostList.getTempCurrentPostsList());

        theModel = pagingCalculation(theModel,(int)thePostPages.getTotalElements(), pageNo);
        theModel.addAttribute("blogList", thePostPages.getContent());
        theModel.addAttribute("nextPage","/blog/bloglist");

        return "blog-list";
    }

    private static Model pagingCalculation(Model theModel, int totalNumberOfBlog, int pageNo){
        int currentNumberOfBlog = (pageNo + 1) * pageSize;
        if ( totalNumberOfBlog > currentNumberOfBlog){
            if (pageNo > 0 ){
                theModel.addAttribute("previous", false);
            }else {
                theModel.addAttribute("previous", true);
            }
            theModel.addAttribute("next", false);
        }else if (totalNumberOfBlog <= currentNumberOfBlog) {
            if (pageNo > 0 ){
                theModel.addAttribute("previous", false);
            }else {
                theModel.addAttribute("previous", true);
            }
            theModel.addAttribute("next", true);
        }
        theModel.addAttribute("nextPageNumber" , (pageNo + 1));
        theModel.addAttribute("previousPageNumber" , (pageNo - 1));
        return theModel;
    }

//#####################################################################################################################
//                                          CRUD OPERATION ON BLOG POSTS
//#####################################################################################################################

    // Add mapping for /showFormForCreateBlog
    @GetMapping("/showFormForCreateBlog")
    public ModelAndView showFormForCreateBlog(Model theModel){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create-blog-post.html");
        modelAndView.addObject("posts",new Posts());
        modelAndView.addObject("allTaglist", theTagsService.findAll());
        return modelAndView;
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
    public String showFormForUpdate(@RequestParam("postId") int theId,
                                    Model theModel){
        Posts thePosts = thePostsService.findById(theId);
        theModel.addAttribute("allTaglist", theTagsService.findAll());
        theModel.addAttribute("posts", thePosts);
        return "create-blog-post";
    }

    // Add mapping for /save
    @PostMapping("/save")
    public String saveBlogPost(@ModelAttribute("posts") Posts thePosts,
                               @RequestParam("publishedDate")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME ) LocalDateTime publishedDate
                               ){
        thePosts.setExcerpt(thePosts.getContent());
        thePosts.setPublishedAt(Timestamp.valueOf(publishedDate));
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
//#####################################################################################################################
//                                        CRUD OPERATION ON COMMENT SECTION
//#####################################################################################################################

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

    // Add mapping for /deleteComment
    @GetMapping("/deleteComment")
    public String deleteComment(@RequestParam("commentId") int theId, Model theModel){
        int postId = theCommentsService.findById(theId).getPostId();
        theCommentsService.deleteById(theId);
        return "redirect:/blog/showFullBlogPost?postId=" + postId;
    }

//#####################################################################################################################
//                                          SEARCHING, SORTING AND FILTERING
//#####################################################################################################################

    // Add mapping for /search
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword,
                         @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                         @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                         Model theModel){

        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theSearchedList = thePostsService.searchInCurrentList(theCurrentPostList.getTempCurrentPostsList(), keyword);
        theCurrentPostList.setTempCurrentPostsList(theSearchedList);
        Page<Posts> theSearchPostPage = thePostsService.getCurrentPostList(paging,theSearchedList);

        theModel = pagingCalculation(theModel,(int) theSearchPostPage.getTotalElements(), pageNo);
        theModel.addAttribute("blogList", theSearchPostPage.getContent());
        theModel.addAttribute("keyword", keyword);
        theModel.addAttribute("nextPage","/blog/search?keyword="+keyword);

        return "blog-list";
    }

    // Add mapping for /sort
    @GetMapping(value = {"/sort"})
    public String sortBy(@RequestParam("sortBy") String sortBy,
                         @RequestParam("order") String order,
                         @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                         @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                         Model theModel){

        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theSearchedList = thePostsService.sortPostsList(theCurrentPostList.getTempCurrentPostsList(), sortBy, order);
        theCurrentPostList.setTempCurrentPostsList(theSearchedList);
        Page<Posts> theSearchPostPage = thePostsService.getCurrentPostList(paging,theSearchedList);

        theModel = pagingCalculation(theModel,(int) theSearchPostPage.getTotalElements(), pageNo);
        theModel.addAttribute("blogList", theSearchPostPage.getContent());
        theModel.addAttribute("sortBy",sortBy);
        theModel.addAttribute("order",order);
        theModel.addAttribute("nextPage","/blog/sort?sortBy="+sortBy+"&order="+order);

        return "blog-list";
    }

    // Add mapping for /filter
    @GetMapping("/filter")
    public String filter(@RequestParam("keyword") String keyword,
                         @RequestParam("filterBy") String filterBy,
                         @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                         @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                         Model theModel){

        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theFilteredPostList=null;
        if ("author".equals(filterBy)){
            theFilteredPostList = thePostsService.findByAuthor(keyword);
        }else if ("tag".equals(filterBy)){
            theFilteredPostList = thePostsService.findByTag(keyword);
        }
        theCurrentPostList.setTempCurrentPostsList(theFilteredPostList);
        Page<Posts> theFilteredPostPage = thePostsService.getCurrentPostList(paging,theFilteredPostList);

        theModel = pagingCalculation(theModel,(int) theFilteredPostPage.getTotalElements(), pageNo);
        theModel.addAttribute("blogList", theFilteredPostPage.getContent());
        theModel.addAttribute("keyword", keyword);
        theModel.addAttribute("nextPage","/blog/filter");

        return "blog-list";
    }

    // Add mapping for /filterByDate
    @GetMapping("/filterByDate")
    public String filterByDate(@RequestParam("from")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME ) LocalDateTime from,
                               @RequestParam("to")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME ) LocalDateTime to,
                               @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                               @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                               Model theModel){

        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theFilteredPostList=thePostsService.filterByDatePostList(theCurrentPostList.getTempCurrentPostsList(),
                Timestamp.valueOf(from),Timestamp.valueOf(to));

        theCurrentPostList.setTempCurrentPostsList(theFilteredPostList);
        Page<Posts> theFilteredPostPage = thePostsService.getCurrentPostList(paging,theFilteredPostList);

        theModel = pagingCalculation(theModel,(int) theFilteredPostPage.getTotalElements(), pageNo);
        theModel.addAttribute("blogList", theFilteredPostPage.getContent());
        theModel.addAttribute("from",from);
        theModel.addAttribute("to",to);
        theModel.addAttribute("nextPage","/blog/filterByDate?from="+from+"&to="+to);

        return "blog-list";
    }
}






