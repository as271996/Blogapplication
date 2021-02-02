package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface PostsRepository extends JpaRepository<Posts, Integer> {
    // that's it .. no need to write any code LOL!.
    @Query("SELECT p FROM Posts p WHERE CONCAT(p.title, ' ', p.content, ' ', p.author) LIKE %?1%")
    public Page<Posts> search(String keyword, Pageable pageable);

}
