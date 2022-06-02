package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public record HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {}
