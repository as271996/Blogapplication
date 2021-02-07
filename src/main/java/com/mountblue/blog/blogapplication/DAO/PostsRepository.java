package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Posts;
import com.mountblue.blog.blogapplication.entity.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


public interface PostsRepository extends JpaRepository<Posts, Integer> {
    // that's it .. no need to write any code LOL!.
    @Query("SELECT p FROM Posts p WHERE CONCAT(p.title, ' ', p.content, ' ', p.author, ' ',p.publishedAt) LIKE %?1%")
    public Page<Posts> search(String keyword, Pageable pageable);

    @Query("SELECT p FROM Posts p WHERE p.author LIKE %?1%" )
    public List<Posts> findByAuthor(String theAuthor);

    @Query("SELECT p FROM Posts p WHERE p.publishedAt LIKE %?1%" )
    public Page<Posts> findByPublishedAt(Timestamp thePublishedAt, Pageable pageable);

    @Query("select distinct p from Posts p join p.tagsList t where t.name in (?1)")
    public List<Posts> findByTag(String tags);

}
