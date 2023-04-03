package com.cleanup.controller;

import com.cleanup.service.interfaces.UserService;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;
import com.cleanup.utility.helpers.MustacheHelper;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController extends BaseController {

    public HomeController(ModelMapper userMapper, UserService userService, MustacheHelper mustache) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.mustache = mustache;
    }

    private final ModelMapper userMapper;
    private final UserService userService;
    private final MustacheHelper mustache;


    @GetMapping("/")
    public ResponseEntity<String> home() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileHome();
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileLogin();
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @PostMapping("/do-login")
    public ResponseEntity<String> doLogin(@RequestParam String login, @RequestParam String password) throws NotFoundException, NotValidException {
        userService.doLogin(login, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileHome();
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> doLogout(HttpSession session) {
        session.invalidate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        String html = mustache.compileHome();
        return new ResponseEntity<>(html, headers, HttpStatus.OK);
    }
}
