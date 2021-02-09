package com.mountblue.blog.blogapplication.entity;


import java.util.ArrayList;
import java.util.List;


public class CurrentPostList {

    private List<Posts> tempCurrentPostsList;

    public CurrentPostList() {
    }

    public CurrentPostList(List<Posts> tempCurrentPostsList) {
        this.tempCurrentPostsList = tempCurrentPostsList;
    }

    public List<Posts> getTempCurrentPostsList() {
        return tempCurrentPostsList;
    }

    public void setTempCurrentPostsList(List<Posts> tempCurrentPostsList) {
        this.tempCurrentPostsList = tempCurrentPostsList;
    }

    public void add(Posts thePosts) {
        if (tempCurrentPostsList == null) {
            tempCurrentPostsList = new ArrayList<>();
        }
        tempCurrentPostsList.removeIf(id -> (tempCurrentPostsList.stream().anyMatch(t -> t.getId()
                == (thePosts.getId()))));
        tempCurrentPostsList.add(thePosts);
    }

    public void delete(int postId) {
        tempCurrentPostsList.removeIf(id -> (tempCurrentPostsList.stream().anyMatch(t -> t.getId() == postId)));

    }

}
