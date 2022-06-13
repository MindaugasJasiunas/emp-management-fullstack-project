package com.example.demo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record HttpAuthRegisterRequest(
        @NotNull(message = "username must not be empty") String username,
        @NotNull(message = "email must not be empty") @Email(message = "wrong email format") String email,
        @NotNull(message = "password must not be empty") @Size(min = 8, message = "password should be at least 8 characters long") String password,
        @NotNull(message = "first name must not be empty") String firstName,
        @NotNull(message = "last name must not be empty") String lastName,
        String profileImageUrl,
        @NotNull(message = "date of birth must not be empty (format: yyyy-mm-dd)") LocalDate dateOfBirth
) {}
