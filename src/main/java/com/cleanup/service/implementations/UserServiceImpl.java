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

import static com.cleanup.utility.Constants.*;

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
        mailService.sendAccountVerification(user, userServiceHelper.generateToken());
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
        users.stream()
                .peek(mailService::sendWelcomeMessage)
                .filter(user -> user.getAccountStatus() == AccountStatus.UNVERIFIED)
                .forEach(user -> mailService.sendAccountVerification(user, userServiceHelper.generateToken()));
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

    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    public void deleteByIdBulk(List<Long> ids) {
        userRepository.deleteByIdIn(ids);
    }

    @Transactional
    public void softDeleteById(long id) {
        User user = userRepository.findById(id);
        user.setAccountStatus(AccountStatus.DELETED);
        userRepository.save(user);
        log.info("User 'soft' deleted: " + id);
    }

    @Transactional
    public void softDeleteById(List<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        if (users.size() < ids.size()) {
            log.warn("Some of the users were not found.");
        }
        for (User user : users) {
            user.setAccountStatus(AccountStatus.DELETED);
        }
        userRepository.saveAll(users);
        log.info("Users 'soft' deleted: " + ids);
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
        } else if (user.getAccountStatus() != AccountStatus.VERIFIED) {
            throw new NotValidException("Please verify your account before changing password.");
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

    @Transactional(noRollbackFor = NotValidException.class)
    public void verify(long token) throws NotFoundException, NotValidException {
        User user = findByToken(token);
        if (user == null) {
            throw new NotFoundException("User not found with token: " + token);
        } else if (LocalDateTime.now().isAfter(user.getTokenGeneratedDate().plus(Duration.ofHours(ONE_HOUR)))) {
            long newToken = userServiceHelper.generateToken();
            user.setToken(newToken);
            user.setTokenGeneratedDate(LocalDateTime.now());
            mailService.resendAccountVerification(user, newToken);
            userRepository.save(user);
            throw new NotValidException("Account verification token timed out.");
        } else {
            modifyAccountStatus(user, AccountStatus.VERIFIED);
        }
    }

    public void updateBannedById(long id, boolean status) {
        User user = userRepository.findById(id);
        user.setBanned(status);
        userRepository.save(user);
        log.info("User banned: " + id);
    }

    @Transactional
    public void updateBannedByIdBulk(List<Long> ids, boolean status) {
        List<User> users = userRepository.findByIdIn(ids);
        if (users.size() < ids.size()) {
            log.warn("Some of the users were not found.");
        }
        for (User user : users) {
            user.setBanned(status);
        }
        userRepository.saveAll(users);
        log.info(status ? "Users unbanned: " + ids : "Users banned: " + ids);
    }

    @Transactional
    public void updateBannedByEmailBulk(List<String> emails, boolean status) {
        List<User> users = userRepository.findByEmailIn(emails);
        if (users.size() < emails.size()) {
            log.warn("Some of the users were not found.");
        }
        for (User user : users) {
            user.setBanned(status);
        }
        userRepository.saveAll(users);
        log.info(status ? "Users unbanned: " + emails : "Users banned: " + emails);
    }

    public boolean doLogin(String login, String password) throws NotFoundException, NotValidException {
        User user;
        user = login.contains("@") ? userRepository.findByEmail(login) : userRepository.findByUsername(login);
        if (user == null) {
            log.warn("User not found with login: " + login);
            throw new NotFoundException("User not found");
        }
        if (user.isAccountLocked()) {
            throw new NotValidException("Account locked");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setFailedLoginAttempts((byte) (user.getFailedLoginAttempts() + 1));
            if (user.getFailedLoginAttempts() == 3) {
                mailService.sendWarnAccountLock(user);
            }
            if (user.getFailedLoginAttempts() == 5) {
                mailService.sendAccountLocked(user);
                user.setAccountLocked(true);
            }
            userRepository.save(user);
            return false;
        }
        user.setFailedLoginAttempts((byte) 0);
        user.setLatestLogin(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    private void validatePassword(String password) throws NotValidException {
        if (password.length() < 8) {
            throw new NotValidException("Password must contain at least 8 characters.");
        } else if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new NotValidException("Password must contain at least 8 characters, 1 uppercase, 1 lowercase and a number.\nAny other character is encouraged but not obligatory.");
        }
    }

    private void validateEmailAndUsername(String email, String username) throws DuplicateException, NotValidException {
        if (!email.matches(EMAIL_REGEX)) {
            log.error("Invalid email: " + email);
            throw new NotValidException("Invalid email. Did not match regex");
        }
        if (!username.matches(USERNAME_REGEX)) {
            log.error("Invalid username: " + email);
            throw new NotValidException("Invalid username. Did not match regex");
        }
        if (findByEmail(email) != null) {
            log.error("Duplicate email.");
            throw new DuplicateException("Email already exists. Please enter another email or recover your account.");
        }
        if (findByUsername(username) != null) {
            log.error("Duplicate username.");
            throw new DuplicateException("Username already exists.");
        }
    }

    private void modifyAccountStatus(User user, AccountStatus newStatus) {
        user.setAccountStatus(newStatus);
        userRepository.save(user);
    }
}
