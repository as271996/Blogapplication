package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tags, Integer> {
    // that's it .. no need to write any code LOL!.
}
