package com.cleanup.controller;

import com.cleanup.model.User;
import com.cleanup.model.dto.UserResponse;
import com.cleanup.service.interfaces.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.cleanup.utility.Constants.*;

@RestController
@RequestMapping("/admin")
@RolesAllowed({ROLE_SUPERADMIN, ROLE_ADMIN})
public class AdminController extends BaseController {

    public AdminController(UserService userService, ModelMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    private final ModelMapper userMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/subscribed/{value}")
    public ResponseEntity<List<UserResponse>> findAllSubscribed(@PathVariable boolean value) {
        List<User> users = userService.findAllSubscribed(value);
        List<UserResponse> result = users.stream()
                .map(user -> userMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

}
