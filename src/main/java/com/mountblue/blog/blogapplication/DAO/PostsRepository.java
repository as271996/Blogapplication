package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Integer> {
    // that's it .. no need to write any code LOL!.
}
