package com.cleanup.service.interfaces;

import com.cleanup.model.User;
import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;

import java.util.List;

public interface UserService {

    void save(User userRequest) throws DuplicateException, NotValidException;

    void saveBulk(List<User> userRequests) throws DuplicateException;

    User findById(long id);

    List<User> findByIdBulk(List<Long> ids);

    User findByEmail(String email) throws NotFoundException;

    List<User> findByEmailBulk(List<String> emails);

    List<User> findAll();

    User findByUsername(String username);
    
    List<User> findByUsernameBulk(List<String> usernames);
    
    List<User> findAllSubscribed(boolean subscribed);

    void deleteById(int id);

    void deleteByIdBulk(List<Long> ids);

    void softDeleteById(int id);

    void softDeleteById(List<Long> ids);

    void banById(int id);

    void banByIdBulk(List<Long> ids);

    void banByEmail(String email);

    void banByEmailBulk(List<String> emails);

    void unbanById(int id);

    void unbanByIdBulk(List<Long> ids);

    void unbanByEmail(String email);

    void unbanByEmailBulk(List<String> emails);

    void requestPasswordChange(String email) throws NotFoundException;

    void completePasswordChange();

}
