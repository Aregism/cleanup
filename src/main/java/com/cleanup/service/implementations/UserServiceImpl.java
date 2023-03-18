package com.cleanup.service.implementations;

import com.cleanup.model.User;
import com.cleanup.model.enums.AccountStatus;
import com.cleanup.repository.UserRepository;
import com.cleanup.service.interfaces.MailService;
import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;
import com.cleanup.utility.helpers.UserServiceHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static com.cleanup.utility.Constants.PASSWORD_REGEX;

@Service
public class UserServiceImpl implements UserService {

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserServiceHelper userServiceHelper, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userServiceHelper = userServiceHelper;
        this.mailService = mailService;
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceHelper userServiceHelper;
    private final MailService mailService;

    public void save(User user) throws DuplicateException, NotValidException {
        User fromDb = userRepository.findByEmail(user.getEmail());
        if (fromDb != null){
            if (user.getEmail().equals(fromDb.getEmail())) {
                throw new DuplicateException(String.format("Duplicate email: %s", user.getEmail()));
            } else if (user.getUsername().equals(fromDb.getUsername())) {
                throw new DuplicateException(String.format("Duplicate username: %s", user.getUsername()));
            }
        }
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        } else {
            user.setCustomUsername(true);
        }
        mailService.sendWelcomeMessage(user);
        userRepository.save(user);
    }

    @Transactional
    public void saveBulk(List<User> users) throws DuplicateException {
        List<User> fromDb = findByEmailBulk(users.stream().map(User::getEmail).toList());
        List<String> emails = fromDb.stream().map(User::getEmail).toList();
        if (!fromDb.isEmpty()) throw new DuplicateException(String.format("Duplicate emails found: %s", emails));
        userRepository.saveAll(users);
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> findByIdBulk(List<Long> ids) {
        return userRepository.findByIdIn(ids);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByEmailBulk(List<String> emails) {
        return userRepository.findByEmailIn(emails);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByUsernameBulk(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames);
    }

    public List<User> findAllSubscribed(boolean subscribed) {
        return userRepository.findAllByIsSubscribed(subscribed);
    }

    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    public void deleteByIdBulk(List<Long> ids) {
        userRepository.deleteByIdIn(ids);
    }

    @Transactional
    public void softDeleteById(int id) {
        User user = userRepository.findById(id);
        user.setAccountStatus(AccountStatus.DELETED);
        userRepository.save(user);
    }

    @Transactional
    public void softDeleteById(List<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        for (User user : users) {
            user.setAccountStatus(AccountStatus.DELETED);
        }
        userRepository.saveAll(users);
    }

    public void banById(int id) {
        User user = userRepository.findById(id);
        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void banByIdBulk(List<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        for (User user : users) {
            user.setBanned(true);
        }
        userRepository.saveAll(users);
    }

    public void banByEmail(String email) {
        User user = userRepository.findByEmail(email);
        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void banByEmailBulk(List<String> emails) {
        List<User> users = userRepository.findByEmailIn(emails);
        for (User user : users) {
            user.setBanned(true);
        }
        userRepository.saveAll(users);
    }

    public void unbanById(int id) {
        User user = userRepository.findById(id);
        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void unbanByIdBulk(List<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        for (User user : users) {
            user.setBanned(false);
        }
        userRepository.saveAll(users);
    }

    public void unbanByEmail(String email) {
        User user = userRepository.findByEmail(email);
        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void unbanByEmailBulk(List<String> emails) {
        List<User> users = userRepository.findByEmailIn(emails);
        for (User user : users) {
            user.setBanned(false);
        }
        userRepository.saveAll(users);
    }

    @Transactional
    public void requestPasswordChange(String email) throws NotFoundException {
        User user = findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found by email: " + email);
        }
        user.setToken(userServiceHelper.generateToken());
        user.setTokenGeneratedDate(LocalDateTime.now());
        // TODO: 16-Mar-23 Notify the user here
        userRepository.save(user);
    }

    public void completePasswordChange() {

    }

    private void validatePassword(String password) throws NotValidException {
        if (password.length() < 8){
            throw new NotValidException("Password must contain at least 8 characters.");
        } else if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new NotValidException("Password must contain at least 8 characters, 1 uppercase, 1 lowercase and a number.\nAny other character is encouraged but not obligatory.");
        }
    }
}
