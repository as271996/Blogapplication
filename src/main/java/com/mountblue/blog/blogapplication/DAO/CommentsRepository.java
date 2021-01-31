package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    // that's it .. no need to write any code LOL!.
}
