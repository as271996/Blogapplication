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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@SessionAttributes({"theCurrentPostList"})
@RequestMapping("/blog")
public class BlogApplicationController {

    private static final int pageSize = 10;
    private PostsService thePostsService;
    private CommentsService theCommentsService;
    private TagsService theTagsService;

    @Autowired
    public BlogApplicationController(PostsService thePostsService, CommentsService theCommentsService,
                                     TagsService theTagsService) {
        this.thePostsService = thePostsService;
        this.theCommentsService = theCommentsService;
        this.theTagsService = theTagsService;
    }

    @ModelAttribute("theCurrentPostList")
    public CurrentPostList setCurrentPostList() {
        return new CurrentPostList();
    }

    //#####################################################################################################################
//                                             PRINTING ALL BLOG POSTS HERE
//#####################################################################################################################
    // Add mapping for /bloglist
    @GetMapping(value = {"/bloglist"})
    public String blogList(@RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                           @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                           Model theModel) {

        Pageable paging = PageRequest.of(pageNo, pageSize);
        theCurrentPostList.setTempCurrentPostsList(thePostsService.findAll());
        Page<Posts> thePostPages = thePostsService.getCurrentPostList(paging, theCurrentPostList.getTempCurrentPostsList());

        theModel = thePostsService.pagingCalculation(theModel, (int) thePostPages.getTotalElements(), pageNo, pageSize);
        theModel.addAttribute("blogList", thePostPages.getContent());
        theModel.addAttribute("nextPage", "/blog/bloglist");

        return "blog-list";
    }


//#####################################################################################################################
//                                          CRUD OPERATION ON BLOG POSTS
//#####################################################################################################################

    // Add mapping for /showFormForCreateBlog
    @GetMapping("/showFormForCreateBlog")
    public ModelAndView showFormForCreateBlog(Model theModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Posts posts = new Posts();
        posts.setAuthor(authentication.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create-blog-post.html");
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("allTaglist", theTagsService.findAll());
        return modelAndView;
    }

    // Add mapping for /showFullBlogPost
    @GetMapping("/showFullBlogPost")
    public String showFullBlogPost(@RequestParam("postId") int theId, Model theModel) {
        Posts thePost = thePostsService.findById(theId);
        theModel.addAttribute("post", thePost);
        return "full-blog-post";
    }

    // Add mapping for /showFormForUpdate
    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("postId") int theId,
                                    Model theModel) {
        Posts thePosts = thePostsService.findById(theId);
        theModel.addAttribute("allTaglist", theTagsService.findAll());
        theModel.addAttribute("posts", thePosts);
        return "create-blog-post";
    }

    // Add mapping for /save
    @PostMapping("/save")
    public String saveBlogPost(@ModelAttribute("posts") Posts thePosts,
                               @RequestParam("publishedDate")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishedDate
    ) {
        thePosts.setExcerpt(thePosts.getContent());

        thePosts.setPublishedAt(Timestamp.valueOf(publishedDate));

        if (thePosts.getPublishedAt().after(new Timestamp(System.currentTimeMillis()))) {
            thePosts.setPublished(false);
        } else {
            thePosts.setPublished(true);
        }
        if (thePosts.getCreatedAt() == null) {
            thePosts.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        thePosts.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        thePostsService.save(thePosts);
        return "redirect:/blog/bloglist";
    }

    // Add mapping for /delete
    @GetMapping("/delete")
    public String delete(@RequestParam("postId") int theId, Model theModel) {
        thePostsService.deleteById(theId);
        return "redirect:/blog/bloglist";
    }
//#####################################################################################################################
//                                        CRUD OPERATION ON COMMENT SECTION
//#####################################################################################################################

    // Add mapping for /showFormForComment
    @GetMapping("/showFormForComment")
    public String showFormForComment(@RequestParam("postId") int theId, Model theModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Comments theComments = new Comments();
        theComments.setPostId(theId);
        if (!authentication.getName().equals("anonymousUser")) theComments.setName(authentication.getName());
        theModel.addAttribute("comments", theComments);
        return "comment-form";
    }

    // Add mapping for /showFormForUpdateComment
    @GetMapping("/showFormForUpdateComment")
    public String showFormForUpdateComment(@RequestParam("commentId") int theId,
                                           Model theModel) {
        Comments theComments = theCommentsService.findById(theId);
        theModel.addAttribute("comments", theComments);
        return "comment-form";
    }

    // Add mapping for /saveComment
    @PostMapping("/saveComment")
    public String saveComment(@ModelAttribute("comments") Comments theComments) {
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
    public String deleteComment(@RequestParam("commentId") int theId, Model theModel) {
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
                         Model theModel) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theSearchedList = thePostsService.searchInCurrentList(theCurrentPostList.getTempCurrentPostsList(), keyword);
        theCurrentPostList.setTempCurrentPostsList(theSearchedList);
        Page<Posts> theSearchPostPage = thePostsService.getCurrentPostList(paging, theSearchedList);

        theModel = thePostsService.pagingCalculation(theModel, (int) theSearchPostPage.getTotalElements(), pageNo, pageSize);
        theModel.addAttribute("blogList", theSearchPostPage.getContent());
        theModel.addAttribute("keyword", keyword);
        theModel.addAttribute("nextPage", "/blog/search?keyword=" + keyword);

        return "blog-list";
    }

    // Add mapping for /sort
    @GetMapping(value = {"/sort"})
    public String sortBy(@RequestParam("sortBy") String sortBy,
                         @RequestParam("order") String order,
                         @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                         @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                         Model theModel) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theSearchedList = thePostsService.sortPostsList(theCurrentPostList.getTempCurrentPostsList(), sortBy, order);
        theCurrentPostList.setTempCurrentPostsList(theSearchedList);
        Page<Posts> theSearchPostPage = thePostsService.getCurrentPostList(paging, theSearchedList);

        theModel = thePostsService.pagingCalculation(theModel, (int) theSearchPostPage.getTotalElements(), pageNo, pageSize);
        theModel.addAttribute("blogList", theSearchPostPage.getContent());
        theModel.addAttribute("sortBy", sortBy);
        theModel.addAttribute("order", order);
        theModel.addAttribute("nextPage", "/blog/sort?sortBy=" + sortBy + "&order=" + order);

        return "blog-list";
    }

    // Add mapping for /filter
    @GetMapping("/filter")
    public String filter(@RequestParam("keyword") String keyword,
                         @RequestParam("filterBy") String filterBy,
                         @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                         @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                         HttpServletRequest request,
                         Model theModel) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theFilteredPostList = null;
        String nextPage = "";
        if ("author".equals(filterBy)) {
            nextPage = "/blog/filter?keyword=" + keyword + "&filterBy=" + filterBy;
            theFilteredPostList = thePostsService.findByAuthor(keyword);
        } else if ("tag".equals(filterBy)) {
            String[] tag = keyword.split("#");
            nextPage = "/blog/filter?keyword=%23" + tag[tag.length - 1] + "&filterBy=" + filterBy;
            theFilteredPostList = thePostsService.findByTag(keyword);
        }
        theCurrentPostList.setTempCurrentPostsList(theFilteredPostList);
        Page<Posts> theFilteredPostPage = thePostsService.getCurrentPostList(paging, theFilteredPostList);

        theModel = thePostsService.pagingCalculation(theModel, (int) theFilteredPostPage.getTotalElements(), pageNo, pageSize);
        theModel.addAttribute("blogList", theFilteredPostPage.getContent());
        theModel.addAttribute("keyword", keyword);
        theModel.addAttribute("filterBy", filterBy);
        theModel.addAttribute("nextPage", nextPage);

        return "blog-list";
    }

    // Add mapping for /filterByDate
    @GetMapping("/filterByDate")
    public String filterByDate(@RequestParam("from")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                               @RequestParam("to")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                               @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                               @ModelAttribute("theCurrentPostList") CurrentPostList theCurrentPostList,
                               Model theModel) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        List<Posts> theFilteredPostList = thePostsService.filterByDatePostList(theCurrentPostList.getTempCurrentPostsList(),
                Timestamp.valueOf(from), Timestamp.valueOf(to));

        theCurrentPostList.setTempCurrentPostsList(theFilteredPostList);
        Page<Posts> theFilteredPostPage = thePostsService.getCurrentPostList(paging, theFilteredPostList);

        theModel = thePostsService.pagingCalculation(theModel, (int) theFilteredPostPage.getTotalElements(), pageNo, pageSize);
        theModel.addAttribute("blogList", theFilteredPostPage.getContent());
        theModel.addAttribute("from", from);
        theModel.addAttribute("to", to);
        theModel.addAttribute("nextPage", "/blog/filterByDate?from=" + from + "&to=" + to);

        return "blog-list";
    }
}






