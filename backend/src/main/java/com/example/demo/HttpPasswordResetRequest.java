package com.example.demo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record HttpPasswordResetRequest(
        @NotNull(message = "email must be provided")
        @Email(message = "email is malformed")
        String email
) {}
