package com.firerms.security.repository;

import com.firerms.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUsername(String username);

    User findByUsername(String username);

    User findByUsernameAndFdid(String username, Long fdid);
}
