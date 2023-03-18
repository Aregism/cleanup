package com.cleanup.repository;

import com.cleanup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);
    List<User> findByIdIn(List<Long> ids);

    User findByEmail(String username);
    List<User> findByEmailIn(List<String> emails);

    User findByUsername(String username);
    List<User> findByUsernameIn(List<String> usernames);

    List<User> findAll();
    List<User> findAllByIsSubscribed(boolean subscribed);

    void deleteById(long id);
    void deleteByIdIn(List<Long> ids);

}
