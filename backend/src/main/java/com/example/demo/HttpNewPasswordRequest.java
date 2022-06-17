package com.example.demo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record HttpNewPasswordRequest(
        @NotNull
        @Size(min = 8, message = "password should be at least 8 characters long")
        String newPassword,
        @NotNull
        String newPasswordRepeated
) {}
