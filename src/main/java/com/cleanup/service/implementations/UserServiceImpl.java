package com.cleanup.service.implementations;

import com.cleanup.model.User;
import com.cleanup.model.dto.PasswordChangeRequest;
import com.cleanup.model.enums.AccountStatus;
import com.cleanup.repository.UserRepository;
import com.cleanup.service.interfaces.MailService;
import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;
import com.cleanup.utility.helpers.UserServiceHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static com.cleanup.utility.Constants.ONE_HOUR;
import static com.cleanup.utility.Constants.PASSWORD_REGEX;

@Log4j2
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
        validateEmailAndUsername(user.getEmail(), user.getUsername());
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        } else {
            user.setCustomUsername(true);
        }
        mailService.sendWelcomeMessage(user);
        userRepository.save(user);
        log.info("User created " + user.getEmail());
    }

    @Transactional
    public void saveBulk(List<User> users) throws DuplicateException {
        List<User> fromDb = findByEmailBulk(users.stream().map(User::getEmail).toList());
        List<String> emails = fromDb.stream().map(User::getEmail).toList();
        if (!fromDb.isEmpty()) {
            log.error("Duplicate emails found. No users were saved.");
            throw new DuplicateException(String.format("Duplicate emails found: %s", emails));
        }
        userRepository.saveAll(users);
        log.info("All users successfully saved.");
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
        log.info("User 'soft' deleted: " + id);
    }

    @Transactional
    public void softDeleteById(List<Long> ids) throws NotFoundException {
        List<User> users = userRepository.findByIdIn(ids);
        if (users.size() < ids.size()) {
            log.error("Some of the users were not found. Did not update any users.");
            throw new NotFoundException("Some of the users were not found. Did not update any users.");
        }
        for (User user : users) {
            user.setAccountStatus(AccountStatus.DELETED);
        }
        userRepository.saveAll(users);
        log.info("Users 'soft' deleted: " + ids);
    }

    public void banById(int id) {
        User user = userRepository.findById(id);
        user.setBanned(true);
        userRepository.save(user);
        log.info("User banned: " + id);
    }

    @Transactional
    public void banByIdBulk(List<Long> ids) throws NotFoundException {
        List<User> users = userRepository.findByIdIn(ids);
        if (users.size() < ids.size()) {
            log.error("Some of the users were not found. Did not update any users.");
            throw new NotFoundException("Some of the users were not found. Did not update any users.");
        }
        for (User user : users) {
            user.setBanned(true);
        }
        userRepository.saveAll(users);
        log.info("Users banned: " + ids);
    }

    public void banByEmail(String email) {
        User user = userRepository.findByEmail(email);
        user.setBanned(true);
        userRepository.save(user);
        log.info("User banned: " + email);
    }

    @Transactional
    public void banByEmailBulk(List<String> emails) throws NotFoundException {
        List<User> users = userRepository.findByEmailIn(emails);
        if (users.size() < emails.size()) {
            log.error("Some of the users were not found. Did not update any users.");
            throw new NotFoundException("Some of the users were not found. Did not update any users.");
        }
        for (User user : users) {
            user.setBanned(true);
        }
        userRepository.saveAll(users);
        log.info("Users banned: " + emails);
    }

    public void unbanById(int id) {
        log.info("User unbanned: " + id);
        User user = userRepository.findById(id);
        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void unbanByIdBulk(List<Long> ids) throws NotFoundException {
        List<User> users = userRepository.findByIdIn(ids);
        if (users.size() < ids.size()) {
            log.error("Some of the users were not found. Did not update any users.");
            throw new NotFoundException("Some of the users were not found. Did not update any users.");
        }
        for (User user : users) {
            user.setBanned(false);
        }
        userRepository.saveAll(users);
        log.info("Users unbanned: " + ids);
    }

    public void unbanByEmail(String email) {
        User user = userRepository.findByEmail(email);
        user.setBanned(false);
        userRepository.save(user);
        log.info("User unbanned: " + email);
    }

    @Transactional
    public void unbanByEmailBulk(List<String> emails) throws NotFoundException {
        List<User> users = userRepository.findByEmailIn(emails);
        if (users.size() < emails.size()) {
            log.error("Some of the users were not found. Did not update any users.");
            throw new NotFoundException("Some of the users were not found. Did not update any users.");
        }
        for (User user : users) {
            user.setBanned(false);
        }
        userRepository.saveAll(users);
        log.info("Users unbanned: " + emails);
    }

    @Override
    public User findByToken(long token) {
        return userRepository.findByToken(token);
    }

    @Transactional
    public void requestPasswordChange(PasswordChangeRequest model) throws NotFoundException, NotValidException {
        User user = findByEmail(model.getEmail());
        if (user == null) {
            throw new NotFoundException("User not found by email: " + model.getEmail());
        } else if (passwordEncoder.matches(model.getNewPassword1(), user.getPassword())) {
            throw new NotValidException("New password can not match with the old one.");
        } else if (!model.getNewPassword1().equals(model.getNewPassword2())) {
            throw new NotValidException("The 2 passwords didn't match.");
        } else {
            validatePassword(model.getNewPassword1());
        }
        user.setPasswordToBeSet(passwordEncoder.encode(model.getNewPassword1()));
        user.setToken(userServiceHelper.generateToken());
        user.setTokenGeneratedDate(LocalDateTime.now());
        mailService.sendPasswordChangeToken(user, user.getToken());
        userRepository.save(user);
    }

    @Transactional(noRollbackFor = {NotValidException.class})
    public void completePasswordChange(long token) throws NotValidException, NotFoundException {
        User user = findByToken(token);
        if (user == null) {
            throw new NotFoundException("User not found with token: " + token);
        } else if (LocalDateTime.now().isAfter(user.getTokenGeneratedDate().plus(Duration.ofHours(ONE_HOUR)))) {
            long newToken = userServiceHelper.generateToken();
            user.setToken(newToken);
            user.setTokenGeneratedDate(LocalDateTime.now());
            mailService.resendPasswordChangeToken(user, newToken);
            userRepository.save(user);
            throw new NotValidException("Password change token timed out.");
        } else {
            mailService.sendPasswordChangeConfirmation(user);
            userServiceHelper.setupPasswordChangeUser(user);
            userRepository.save(user);
        }
    }

    private void validatePassword(String password) throws NotValidException {
        if (password.length() < 8) {
            throw new NotValidException("Password must contain at least 8 characters.");
        } else if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new NotValidException("Password must contain at least 8 characters, 1 uppercase, 1 lowercase and a number.\nAny other character is encouraged but not obligatory.");
        }
    }

    private void validateEmailAndUsername(String email, String username) throws DuplicateException {
        if (findByEmail(email) != null) {
            log.error("Duplicate email.");
            throw new DuplicateException("Email already exists. Please enter another email or recover your account.");
        }
        if (findByUsername(username) != null) {
            log.error("Duplicate username.");
            throw new DuplicateException("Username already exists.");
        }
    }
}
