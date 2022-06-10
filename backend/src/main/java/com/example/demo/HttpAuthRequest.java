package com.example.demo;

import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record HttpAuthRequest(@NotNull String username, @NotNull @Size(min = 8) String password) {}
