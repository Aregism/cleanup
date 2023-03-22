package com.cleanup.service.interfaces;

import com.cleanup.model.User;
import com.cleanup.model.dto.PasswordChangeRequest;
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

    void deleteById(long id);

    void deleteByIdBulk(List<Long> ids);

    void softDeleteById(long id);

    void softDeleteById(List<Long> ids);

    User findByToken(long token);

    void requestPasswordChange(PasswordChangeRequest model) throws NotFoundException, NotValidException;

    void completePasswordChange(long token) throws NotValidException, NotFoundException;

    void verify(long token) throws NotFoundException, NotValidException;

    void updateBannedById(long id, boolean status);

    void updateBannedByIdBulk(List<Long> ids, boolean status) throws NotFoundException;

    void updateBannedByEmailBulk(List<String> emails, boolean status);
}
