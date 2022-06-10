package com.example.demo.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserValidationException extends Exception {
    public UserValidationException(String message) {
        super(message);
    }
}
