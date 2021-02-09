package com.mountblue.blog.blogapplication.DAO;

import com.mountblue.blog.blogapplication.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    @Query("SELECT u FROM Users u WHERE u.userName = ?1")
    public Users getUserByUsername(String username);
}
