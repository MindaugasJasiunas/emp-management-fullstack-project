package com.example.demo.exception.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) //defines response status that we get
public class UserDisabledException extends Exception {
    public UserDisabledException(String message) {
        super(message);
    }
}