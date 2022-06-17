package com.example.demo.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@IdClass(PasswordResetPK.class)
@Table(name = "password_reset")
public class PasswordReset {
    @Id
    private String email;
    @Id
    private String link;
}
