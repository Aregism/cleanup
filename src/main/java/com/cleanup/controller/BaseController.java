package com.cleanup.controller;

import com.cleanup.utility.exceptions.DuplicateException;
import com.cleanup.utility.exceptions.NotFoundException;
import com.cleanup.utility.exceptions.NotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseController {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleException(DuplicateException e) {
        // TODO: 20-Mar-23 Log the exception here
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleException(NotFoundException e) {
        // TODO: 20-Mar-23 Log the exception here
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<String> handleException(NotValidException e) {
        // TODO: 20-Mar-23 Log the exception here
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }

}
