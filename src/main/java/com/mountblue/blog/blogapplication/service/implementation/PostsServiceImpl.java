package com.mountblue.blog.blogapplication.service.implementation;

import com.mountblue.blog.blogapplication.DAO.PostsRepository;
import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class PostsServiceImpl implements PostsService {

    private PostsRepository thePostsRepository;

    @Autowired
    public PostsServiceImpl(PostsRepository thePostsRepository) {
        this.thePostsRepository = thePostsRepository;
    }

    public PostsServiceImpl() {

    }

    @Override
    public List<Posts> findAll() {
        List<Posts> thePostsList = new ArrayList<>();
        List<Posts> teamPostsList = thePostsRepository.findAll();
        for (Posts post: teamPostsList){
            if (post.isPublished()){
                thePostsList.add(post);
            }else if (post.getPublishedAt().before(new Timestamp(System.currentTimeMillis()))){
                post.setPublished(true);
                save(post);
                thePostsList.add(post);
            }
        }
        return thePostsList;
    }

    @Override
    public Posts findById(int theId) {
        Optional<Posts> result = thePostsRepository.findById(theId);
        Posts thePosts = null;
        if (result.isPresent()) {
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

    //##################################################################################################################
//             HERE SEARCHING BLOG POSTS BY AUTHOR-NAME, TITLE, CONTENT, AND TAGS INSIDE LIST OF POST
//##################################################################################################################
    @Override
    public List<Posts> searchInCurrentList(List<Posts> tempCurrentList, String keyword) {
        PostsServiceImpl thePostsServiceImpl = new PostsServiceImpl();
        List<Posts> theCurrentSearchList = new ArrayList<>();
        if (tempCurrentList.size() <= 0) tempCurrentList = thePostsServiceImpl.findAll();
        /*keyword = keyword.strip();*/
        keyword = keyword.replaceAll("^\\s+", "");
        keyword = keyword.replaceAll("^\\\\s+$", "");

        if (keyword == null || keyword.isEmpty()) {
            return tempCurrentList;
        }
        for (Posts thePost : tempCurrentList) {
            String finalKeyword = keyword;
            if (thePost.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                    thePost.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    thePost.getContent().toLowerCase().contains(keyword.toLowerCase()) ||
                    thePost.getTagsList().stream().anyMatch(tag -> tag.getName().toLowerCase()
                            .contains(finalKeyword.toLowerCase()))) {
                theCurrentSearchList.add(thePost);
            }
        }
        return theCurrentSearchList;
    }

    //##########################################################################################################
//                   SORTING BLOG POST BY AUTHOR/PUBLISHED DATE IN ASCENDING/DESCENDING ORDER
//##########################################################################################################
    @Override
    public List<Posts> sortPostsList(List<Posts> thePostsList, String sortBy, String direction) {

        if ("asc".equals(direction)) {
            if ("author".equals(sortBy)) {
                thePostsList.sort(Comparator.comparing(Posts::getAuthor));
            } else {
                thePostsList.sort(Comparator.comparing(Posts::getPublishedAt));
            }
        } else {
            if ("author".equals(sortBy)) {
                thePostsList.sort(Comparator.comparing(Posts::getAuthor).reversed());
            } else {
                thePostsList.sort(Comparator.comparing(Posts::getPublishedAt).reversed());
            }
        }
        return thePostsList;
    }

    //###########################################################################################################
//                                  FILTERING BY AUTHOR, TAG AND FROM-TO DATE
//###########################################################################################################
    @Override
    public List<Posts> findByAuthor(String theAuthor) {
        return thePostsRepository.findByAuthor(theAuthor);
    }

    @Override
    public List<Posts> findByTag(String tags) {
        return thePostsRepository.findByTag(tags);
    }

    @Override
    public List<Posts> filterByDatePostList(List<Posts> thePostsList, Timestamp from, Timestamp to) {

        List<Posts> theFilteredPostList = new ArrayList<>();
        for (Posts post : thePostsList) {
            if (post.getPublishedAt().after(from) && post.getPublishedAt().before(to)) {
                theFilteredPostList.add(post);
            }
        }
        return theFilteredPostList;
    }

    //###########################################################################################################
//                                   DOING PAGINATION ON BLOG POSTS LIST
//###########################################################################################################
    @Override
    public Page<Posts> getCurrentPostList(Pageable pageable, List<Posts> theCurrentPostList) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), theCurrentPostList.size());
        return new PageImpl<>(theCurrentPostList.subList(start, end), pageable, theCurrentPostList.size());
    }

    public Model pagingCalculation(Model theModel, int totalNumberOfBlog, int pageNo, int pageSize) {
        int currentNumberOfBlog = (pageNo + 1) * pageSize;
        if (totalNumberOfBlog > currentNumberOfBlog) {
            if (pageNo > 0) {
                theModel.addAttribute("previous", false);
            } else {
                theModel.addAttribute("previous", true);
            }
            theModel.addAttribute("next", false);
        } else if (totalNumberOfBlog <= currentNumberOfBlog) {
            if (pageNo > 0) {
                theModel.addAttribute("previous", false);
            } else {
                theModel.addAttribute("previous", true);
            }
            theModel.addAttribute("next", true);
        }
        theModel.addAttribute("nextPageNumber", (pageNo + 1));
        theModel.addAttribute("previousPageNumber", (pageNo - 1));
        return theModel;
    }
//###########################################################################################################
}
