package com.example.demo;

import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record HttpAuthLoginRequest(
        @NotNull String username,
        @NotNull String password
) {}
