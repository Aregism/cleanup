package com.cleanup.controller;

import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class BaseController {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleException(DuplicateException e) {
        log.info("Handled exception 'DuplicateException': " + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleException(NotFoundException e) {
        log.info("Handled exception 'NotFoundException': " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<String> handleException(NotValidException e) {
        log.info("Handled exception 'NotValidException': " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }

}
