package com.cleanup.controller;

import com.cleanup.model.User;
import com.cleanup.model.dto.UserRequest;
import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotValidException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    public UserController(UserService userService, ModelMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    private final ModelMapper userMapper;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRequest userRequest) throws DuplicateException, NotValidException {
        userService.save(userMapper.map(userRequest, User.class));
        return ResponseEntity.ok().build();
    }

}
