package com.example.demo.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class PasswordResetPK implements Serializable {
    private static final long serialVersionUID = -3994752794519271165L;
    private String email;
    private String link;

    public PasswordResetPK() {}

    public PasswordResetPK(String email, String link) {
        this.email = email;
        this.link = link;
    }
}
