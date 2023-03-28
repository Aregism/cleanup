package com.cleanup.controller;

import com.cleanup.model.User;
import com.cleanup.model.dto.DataContainer;
import com.cleanup.model.dto.UserRequest;
import com.cleanup.model.dto.UserResponse;
import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.helpers.MustacheHelper;
import jakarta.annotation.security.RolesAllowed;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.cleanup.utility.Constants.ROLE_ADMIN;
import static com.cleanup.utility.Constants.ROLE_SUPERADMIN;

@RestController
@RequestMapping("/admin")
@RolesAllowed({ROLE_SUPERADMIN, ROLE_ADMIN})
public class AdminController extends BaseController {

    public AdminController(UserService userService, ModelMapper userMapper, MustacheHelper mustache) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.mustache = mustache;
    }

    private final ModelMapper userMapper;
    private final UserService userService;
    private final MustacheHelper mustache;

    @PostMapping("/save-bulk")
    public ResponseEntity<Void> saveBulk(@RequestBody DataContainer<UserRequest> userRequests) throws DuplicateException {
        List<User> result = userRequests.getData().stream()
                .map(request -> userMapper.map(request, User.class))
                .toList();
        userService.saveBulk(result);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<String> findAll() {
        List<User> users = userService.findAll();
        List<UserResponse> result = users.stream()
                .map(user -> userMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileMultipleUsers("MultipleUsers", result, "all");
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<String> findById(@PathVariable long id) throws NotFoundException {
        User user = userService.findById(id);
        if (user == null) throw new NotFoundException("User not found with ID: " + id);
        UserResponse model = userMapper.map(user, UserResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileSingleUser("SingleUser", model);
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @PostMapping("/bulk-id")
    public ResponseEntity<String> findByIdBulk(@RequestBody DataContainer<Long> ids) {
        List<User> users = userService.findByIdBulk(ids.getData());
        List<UserResponse> result = users.stream()
                .map(user -> userMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileMultipleUsers("MultipleUsers", result, "by ids");
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @PostMapping("/by-email")
    public ResponseEntity<String> findByEmailBulk(@RequestBody DataContainer<String> emails) {
        List<User> users = userService.findByEmailBulk(emails.getData());
        List<UserResponse> result = users.stream()
                .map(user -> userMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileMultipleUsers("MultipleUsers", result, "by emails");
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @PostMapping("/by-username")
    public ResponseEntity<String> findByUsernameBulk(@RequestBody DataContainer<String> usernames) {
        List<User> users = userService.findByUsernameBulk(usernames.getData());
        List<UserResponse> result = users.stream()
                .map(user -> userMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileMultipleUsers("MultipleUsers", result, "by usernames");
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @GetMapping("/subscribed/{value}")
    public ResponseEntity<String> findAllSubscribed(@PathVariable boolean value) {
        List<User> users = userService.findAllSubscribed(value);
        List<UserResponse> result = users.stream()
                .map(user -> userMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileMultipleUsers("MultipleUsers", result, "by subscribed value");
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @DeleteMapping("/delete/by-id/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/bulk-id")
    public ResponseEntity<List<UserResponse>> deleteByIdBulk(@RequestBody DataContainer<Long> ids) {
        userService.deleteByIdBulk(ids.getData());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/soft-delete/by-id/{id}")
    public ResponseEntity<Void> softDeleteById(@PathVariable long id) {
        userService.softDeleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/soft-delete/bulk-id")
    public ResponseEntity<List<UserResponse>> softDeleteByIdBulk(@RequestBody DataContainer<Long> ids) {
        userService.softDeleteById(ids.getData());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-banned/by-id/{id}")
    public ResponseEntity<Void> updateBannedById(@PathVariable long id, @RequestParam boolean newStatus) {
        userService.updateBannedById(id, newStatus);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-banned/bulk-id")
    public ResponseEntity<List<UserResponse>> updateBannedByIdBulk(@RequestBody DataContainer<Long> ids, @RequestParam boolean newStatus) throws NotFoundException {
        userService.updateBannedByIdBulk(ids.getData(), newStatus);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-banned/by-email")
    public ResponseEntity<Void> updateBannedByEmail(@RequestBody DataContainer<String> emails, @RequestParam boolean newStatus) {
        userService.updateBannedByEmailBulk(emails.getData(), newStatus);
        return ResponseEntity.ok().build();
    }
}
