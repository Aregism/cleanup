package com.cleanup.controller;

import com.cleanup.model.User;
import com.cleanup.model.dto.PasswordChangeRequest;
import com.cleanup.model.dto.UserRequest;
import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;
import com.cleanup.utility.helpers.MustacheHelper;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    public UserController(UserService userService, ModelMapper userMapper, MustacheHelper mustache) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.mustache = mustache;
    }

    private final ModelMapper userMapper;
    private final UserService userService;
    private final MustacheHelper mustache;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRequest userRequest) throws DuplicateException, NotValidException {
        userService.save(userMapper.map(userRequest, User.class));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pw-change-request")
    public ResponseEntity<String> requestPasswordChange() {
        // TODO: 31-Mar-23  create a view for the request password change
        return null;
    }

    @PostMapping("/pw-change-request")
    public ResponseEntity<Void> requestPasswordChange(@RequestBody PasswordChangeRequest model) throws NotFoundException, NotValidException {
        userService.requestPasswordChange(model);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pw-confirm/{token}")
    public ResponseEntity<Void> completePasswordChange(@PathVariable long token) throws NotValidException, NotFoundException {
        userService.completePasswordChange(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<Void> verify(@PathVariable long token) throws NotValidException, NotFoundException {
        userService.verify(token);
        return ResponseEntity.ok().build();
    }


}
